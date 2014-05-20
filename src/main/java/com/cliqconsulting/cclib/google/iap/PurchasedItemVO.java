package com.cliqconsulting.cclib.google.iap;

/**
 * PurchasedItemVO
 * <p/>
 * Created by Flavio Ramos on 5/14/14 18:06.
 */
public class PurchasedItemVO {
	public String sku;
	public PurchaseData purchasedData;
	public String signature;

	public class PurchaseData {
		public String developerPayload;
		public String orderId;
		public String packageName;
		public String productId;
		public int purchaseState;
		public long purchaseTime;
		public String purchaseToken;
	}
}