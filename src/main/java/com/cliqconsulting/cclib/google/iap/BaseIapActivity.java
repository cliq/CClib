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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Missing Link
 * com.cliqconsulting.cclib.google.iap.BaseIapActivity
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
					}
					if (purchaseData == null) {
						onPurchaseFailed();
					} else {
						onPurchaseSuccess(purchaseDataJson);
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
			onPurchaseFailed();
		}
		PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

		try {
			startIntentSenderForResult(pendingIntent.getIntentSender(),
					REQUEST_CODE_PURCHASE, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
					Integer.valueOf(0));
		} catch (Exception e) {
			onPurchaseFailed();
		}

	}

	protected abstract void onProductListRecieved(List<StoreProductVO> storeProductVOs);

	protected abstract void onPurchaseSuccess(PurchaseDataJson purchaseDataJson);

	protected abstract void onPurchaseFailed();

	protected abstract void onProductListError();

	protected abstract String[] getExpectedProductList();

	protected abstract void onServiceReady();

}
