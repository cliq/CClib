package com.cliqconsulting.cclib.util;

import android.os.Handler;
import android.os.Message;

/**
 * CCSimpleHandler
 * <p/>
 * Simple Success/Error handler. No data passed.
 * <p/>
 * Created by Flavio Ramos on 1/29/14 18:33.
 * Copyright (c) 2013 Cliq Consulting. All rights reserved.
 */
public abstract class CCSimpleHandler<T> extends Handler {

	private static final int STATUS_OK = 0;
	private static final int STATUS_ERROR = -1;

	@Override
	public void handleMessage(Message msg) {
		if (msg.what == STATUS_OK) onSuccess((T) msg.obj);
		else if (msg.what == STATUS_ERROR) onError(msg.obj);
	}

	/**
	 * Set task as successful, with result object.
	 */
	public void setSuccess(T result) {
		Message msg = new Message();
		msg.what = STATUS_OK;
		msg.obj = result;
		sendMessage(msg);
	}

	/**
	 * Set task as successful, without result object.
	 */
	public void setSuccess() {
		Message msg = new Message();
		msg.what = STATUS_OK;
		sendMessage(msg);
	}

	/**
	 * Set task as error.
	 */
	public void setError() {
		sendEmptyMessage(STATUS_ERROR);
	}

	/**
	 * Set task as error with detail.
	 */
	public void setError(Object error) {
		Message msg = new Message();
		msg.what = STATUS_ERROR;
		msg.obj = error;
		sendMessage(msg);
	}

	public abstract void onSuccess(T result);

	public abstract void onError(Object error);

}
