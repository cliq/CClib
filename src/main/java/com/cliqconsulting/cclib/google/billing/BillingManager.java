package com.cliqconsulting.cclib.google.billing;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;
import com.cliqconsulting.cclib.framework.EventBus;
import com.cliqconsulting.cclib.util.CCLog;
import com.cliqconsulting.cclib.util.CCSimpleHandler;
import com.google.gson.Gson;
import com.squareup.otto.Produce;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * BillingManager
 * <p/>
 * Created by Flavio Ramos on 5/7/14 15:11.
 */
public abstract class BillingManager {

	private final int REQUEST_CODE_PURCHASE = 1000;
	private static BillingManager mInstance;
	private ServiceConnection mServiceConn;
	private IInAppBillingService mService;
	private List<StoreProductVO> mGoogleProducts;
	private Activity mActivity;

	protected final Application mApplication;
	protected String[] mExpectedSkus;

	public enum ProductType {
		COINS,
		CATEGORIES,
		REMOVE_ADS
	}

	public BillingManager(Application application) {
		mApplication = application;

		EventBus.getInstance().register(this);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_PURCHASE:
                if (data != null) {
                    int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                    String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                    String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

                    PurchaseDataJson purchaseDataJson = null;

                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            purchaseDataJson = new Gson().fromJson(purchaseData, PurchaseDataJson.class);
                            purchaseDataJson.purchaseJson = purchaseData;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (purchaseDataJson == null) {
                            producePurchaseFailedEvent();
                        } else {
                            producePurchaseSuccessEvent(purchaseDataJson, dataSignature);
                        }
                    } else {
                        producePurchaseFailedEvent();
                    }
                } else {
                    producePurchaseFailedEvent();
                }

				break;
		}
	}

	public void onActivityPause() {
		if (mServiceConn != null && mActivity != null) {
			mActivity.unbindService(mServiceConn);
		}
		mActivity = null;
	}

	public void onActivityResume(Activity activity) {
		mServiceConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				mService = null;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mService = IInAppBillingService.Stub.asInterface(service);
				produceServiceReadyEvent();
			}
		};

		mActivity = activity;
		mActivity.bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"),
				mServiceConn, Context.BIND_AUTO_CREATE);
	}

	protected Activity getActivity() {
		return mActivity;
	}

	public void buyProduct(String productId) {
		if (mActivity == null) {
			return;
		}

		Bundle buyIntentBundle = null;
		String uuid = UUID.randomUUID().toString();

		try {
			buyIntentBundle = mService.getBuyIntent(3, mActivity.getPackageName(), productId, "inapp", uuid);
		} catch (RemoteException e) {
			e.printStackTrace();
			producePurchaseFailedEvent();
		}

		PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

		try {
			mActivity.startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_CODE_PURCHASE, new Intent(), 0, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
			producePurchaseFailedEvent();
		}

	}

	public void consumeProduct(String token, final CCSimpleHandler handler) {
		if (mActivity == null) {
			return;
		}

		new AsyncTask<String, Integer, Integer>() {
			@Override protected Integer doInBackground(String... strings) {
                if (mService == null || mActivity == null)
                    this.cancel(true);

				int response = Integer.MIN_VALUE;

				try {
					response = mService.consumePurchase(3, mActivity.getPackageName(), strings[0]);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				return response;
			}

			@Override protected void onPostExecute(Integer integer) {
				if (integer == 0) {
					handler.setSuccess(integer);
				} else {
					handler.setError(integer);
				}
			}
		}.execute(token);
	}

	public void getPurchasedItems(CCSimpleHandler<PurchasedItemVO[]> handler) {
		loadPurchasedItems(null, new PurchasedItemVO[]{}, handler);
	}

	private void loadPurchasedItems(final String continuationToken, final PurchasedItemVO[] items, final CCSimpleHandler<PurchasedItemVO[]> handler) {
		if (mActivity == null) {
			return;
		}

		new AsyncTask<Object, Object, Object>() {
			@Override protected Object doInBackground(Object[] objects) {
                if (mService == null || mActivity == null)
                    this.cancel(true);

				Bundle ownedItems = null;

				try {
					ownedItems = mService.getPurchases(3, mActivity.getPackageName(), "inapp", continuationToken);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				if (ownedItems == null) {
					handler.setError();
				}

				int response = ownedItems.getInt("RESPONSE_CODE");

				if (response == 0) {
					ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
					ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
					ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
					String cToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");

					PurchasedItemVO item;

					PurchasedItemVO[] newItems = new PurchasedItemVO[items.length + purchaseDataList.size()];

					for (int i = 0; i < items.length; i++) {
						newItems[i] = items[i];
					}

					for (int i = 0; i < purchaseDataList.size(); ++i) {
						item = new PurchasedItemVO();

						if (purchaseDataList != null && i < purchaseDataList.size()) {
							try {
								item.purchasedData = new Gson().fromJson(purchaseDataList.get(i), PurchasedItemVO.PurchaseData.class);
							} catch (Exception e) {
								CCLog.logError(e.toString());
							}
						}

						if (signatureList != null && i < signatureList.size()) {
							item.signature = signatureList.get(i);
						}

						if (ownedSkus != null && i < ownedSkus.size()) {
							item.sku = ownedSkus.get(i);
						}

						newItems[(items.length > 0 ? items.length - 1 : 0) + i] = item;
					}

					if (cToken != null) {
						loadPurchasedItems(cToken, newItems, handler);
					} else {
						handler.setSuccess(newItems);
					}
				} else {
					handler.setError();
				}

				return null;
			}
		}.execute();
	}

	public void getAvailableProductList() {
		AsyncTask<Object, Object, List<StoreProductVO>> task = new AsyncTask<Object, Object, List<StoreProductVO>>() {

			@Override
			protected List<StoreProductVO> doInBackground(Object... objects) {
                if (mService == null || mActivity == null)
                    this.cancel(true);

				ArrayList skuList = new ArrayList();
                List<StoreProductVO> result;
                if (mExpectedSkus == null) {
                    result = null;
                } else {
                    result = new ArrayList<StoreProductVO>();

                    for (int i = 0; i < mExpectedSkus.length; i++) skuList.add(mExpectedSkus[i]);

                    Bundle querySkus = new Bundle();

                    querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

                    Bundle skuDetails = null;

                    try {
                        skuDetails = mService.getSkuDetails(3, mActivity.getPackageName(), "inapp", querySkus);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    if (skuDetails != null && skuDetails.getInt("RESPONSE_CODE") == 0) {
                        ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

                        StoreProductVO product;

                        for (String thisResponse : responseList) {
                            try {
                                product = new Gson().fromJson(thisResponse, StoreProductVO.class);
                                result.add(product);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        result = null;
                    }

                }

				return result;
			}

			@Override
			protected void onPostExecute(List<StoreProductVO> storeProductVOs) {
				mGoogleProducts = storeProductVOs;

				if (storeProductVOs != null) {
					produceProductListReceivedEvent();
				} else {
					produceProductListErrorEvent();
				}
			}
		}.execute();

	}

	public List<StoreProductVO> getGoogleProducts() {
		return mGoogleProducts;
	}

	@Produce public void producePurchaseSuccessEvent(PurchaseDataJson purchaseDataJson, String dataSignature) {
		EventBus.getInstance().post(new BillingEvent(BillingEvent.Type.PURCHASE_SUCCESS, purchaseDataJson, dataSignature));
	}

	@Produce public void producePurchaseFailedEvent() {
		EventBus.getInstance().post(new BillingEvent((BillingEvent.Type.PURCHASE_ERROR)));
	}

	@Produce public void produceServiceReadyEvent() {
		EventBus.getInstance().post(new BillingEvent((BillingEvent.Type.SERVICE_READY)));
	}

	@Produce public void produceProductListErrorEvent() {
		EventBus.getInstance().post(new BillingEvent((BillingEvent.Type.PRODUCT_LIST_ERROR)));
	}

	@Produce public void produceProductListReceivedEvent() {
		EventBus.getInstance().post(new BillingEvent((BillingEvent.Type.PRODUCT_LIST_SUCCESS)));
	}

	@Produce public void produceBalanceUpdateEvent() {
		EventBus.getInstance().post(new BillingEvent((BillingEvent.Type.BALANCE_UPDATE)));
	}

}
