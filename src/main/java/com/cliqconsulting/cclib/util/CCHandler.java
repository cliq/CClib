package com.cliqconsulting.cclib.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Missing Link
 * com.cliqconsulting.cclib.util.CCHandler
 * <p/>
 * Generics based handler utility.
 * <p/>
 * <p/>
 * Created by Flavio Ramos on 1/29/14 16:06.
 * Copyright (c) 2013 Cliq Consulting. All rights reserved.
 */
public abstract class CCHandler<ResponseType> extends Handler {

	private Object mValue;

	public CCHandler(Looper mainLooper) {
		super(mainLooper);
	}

	@Override
	public void handleMessage(Message msg) {
		ResponseType resultType = null;
		Object resultValue = null;

		try {
			Object[] obj = (Object[]) msg.obj;
			resultType = (ResponseType) obj[0];
			resultValue = (Object) obj[1];
		} catch (ClassCastException e) {
			CCLog.logDebug("CCHandler", e.getMessage());
		}

		onResult(resultType);
		setResultValue(resultValue);
	}

	public void setResultStatus(ResponseType responseType) {
		setResult(responseType, null);
	}

	public void setResult(ResponseType responseType, Object responseObject) {
		Message msg = new Message();
		msg.obj = new Object[]{responseType, responseObject};
		handleMessage(msg);
	}

	public void setResultValue(Object value) {
		mValue = value;
	}

	public Object getResultValue() {
		return mValue;
	}

	public abstract void onResult(ResponseType responseType);

}
