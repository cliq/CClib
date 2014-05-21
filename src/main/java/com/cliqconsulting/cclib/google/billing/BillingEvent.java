package com.cliqconsulting.cclib.google.billing;

/**
 * BillingEvent
 * <p/>
 * Created by Flavio Ramos on 5/20/14 16:35.
 */
public class BillingEvent {

	private final Type mType;
	private PurchaseDataJson mPurchaseDataJson;
	private String mDataSignature;

	public enum Type {
		SERVICE_READY,
		BALANCE_UPDATE,
		PURCHASE_SUCCESS,
		PURCHASE_ERROR, PRODUCT_LIST_ERROR, PRODUCT_LIST_SUCCESS,
	}

	public BillingEvent(Type type, PurchaseDataJson purchaseDataJson, String dataSignature) {
		mType = type;
		mPurchaseDataJson = purchaseDataJson;
		mDataSignature = dataSignature;
	}

	public BillingEvent(Type type) {
		mType = type;
	}

	public Type getType() {
		return mType;
	}

	public PurchaseDataJson getPurchaseDataJson() {
		return mPurchaseDataJson;
	}

	public String getDataSignature() {
		return mDataSignature;
	}
}
