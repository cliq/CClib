package com.cliqconsulting.cclib.google.iap;

import android.app.Activity;
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
import com.cliqconsulting.cclib.util.CCLog;
import com.cliqconsulting.cclib.util.CCSimpleHandler;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * BaseIapActivity
 * <p/>
 * Created by Flavio Ramos on 1/9/14 11:18.
 * Copyright (c) 2013 Cliq Consulting. All rights reserved.
 */
public abstract class BaseIapActivity extends Activity {

	private final int REQUEST_CODE_PURCHASE = 1000;
	private ServiceConnection mServiceConn;
	private IInAppBillingService mService;

	@Override
	protected void onPause() {
		super.onPause();
		if (mServiceConn != null) {
			unbindService(mServiceConn);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_PURCHASE:
				int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
				String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
				String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

				PurchaseDataJson purchaseDataJson = null;

				if (resultCode == RESULT_OK) {
					try {
						purchaseDataJson = new Gson().fromJson(purchaseData, PurchaseDataJson.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (purchaseDataJson == null) {
						onPurchaseFailed();
					} else {
						onPurchaseSuccess(purchaseDataJson, dataSignature);
					}
				} else {
					onPurchaseFailed();
				}

				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		mServiceConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				mService = null;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mService = IInAppBillingService.Stub.asInterface(service);
				onServiceReady();
			}
		};

		bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"),
				mServiceConn, Context.BIND_AUTO_CREATE);
	}

	public void getAvailableProductList() {

		AsyncTask<Object, Object, List<StoreProductVO>> task = new AsyncTask<Object, Object, List<StoreProductVO>>() {

			@Override
			protected List<StoreProductVO> doInBackground(Object... objects) {
				ArrayList skuList = new ArrayList();

				String[] expected = getExpectedProductList();
				List<StoreProductVO> result = new ArrayList<StoreProductVO>();

				for (int i = 0; i < expected.length; i++) skuList.add(expected[i]);

				Bundle querySkus = new Bundle();

				querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

				Bundle skuDetails = null;

				try {
					skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				int response = skuDetails.getInt("RESPONSE_CODE");

				if (response == 0) {
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

				return result;
			}

			@Override
			protected void onPostExecute(List<StoreProductVO> storeProductVOs) {
				if (storeProductVOs != null) {
					onProductListRecieved(storeProductVOs);
				} else {
					onProductListError();
				}
			}
		}.execute();

	}

	public void buyProduct(StoreProductVO storeProductVO) {
		Bundle buyIntentBundle = null;
		String uuid = UUID.randomUUID().toString();

		try {
			buyIntentBundle = mService.getBuyIntent(3, getPackageName(), storeProductVO.productId, "inapp", uuid);
		} catch (RemoteException e) {
			e.printStackTrace();
			onPurchaseFailed();
		}

		PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

		try {
			startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_CODE_PURCHASE, new Intent(), 0, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
			onPurchaseFailed();
		}

	}

	public void consumeProduct(String token, final CCSimpleHandler handler) {
		new AsyncTask<String, Integer, Integer>() {
			@Override protected Integer doInBackground(String... strings) {
				int response = Integer.MIN_VALUE;

				try {
					response = mService.consumePurchase(3, getPackageName(), strings[0]);
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
		new Runnable() {
			@Override public void run() {
				Bundle ownedItems = null;

				try {
					ownedItems = mService.getPurchases(3, getPackageName(), "inapp", continuationToken);
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
			}
		}.run();
	}

	protected abstract void onProductListRecieved(List<StoreProductVO> storeProductVOs);

	protected abstract void onPurchaseSuccess(PurchaseDataJson purchaseDataJson, String dataSignature);

	protected abstract void onPurchaseFailed();

	protected abstract void onProductListError();

	protected abstract String[] getExpectedProductList();

	protected abstract void onServiceReady();
}
