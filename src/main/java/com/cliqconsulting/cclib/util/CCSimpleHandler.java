package com.cliqconsulting.cclib.util;

import android.os.Handler;
import android.os.Message;

/**
 * Missing Link
 * com.cliqconsulting.cclib.util.CCSimpleHandler
 * <p/>
 * Simple Success/Error handler. No data passed.
 * <p/>
 * <p/>
 * Created by Flavio Ramos on 1/29/14 18:33.
 * Copyright (c) 2013 Fanatee. All rights reserved.
 */
public abstract class CCSimpleHandler extends Handler {

	private static final int STATUS_OK = 0;
	private static final int STATUS_ERROR = -1;

	@Override
	public void handleMessage(Message msg) {
		if (msg.what == STATUS_OK) onSuccess(msg.obj);
		else if (msg.what == STATUS_ERROR) onError();
	}

	/**
	 * Set task as successful, with result object.
	 */
	public void setSuccess(Object result) {
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

	public abstract void onSuccess(Object result);

	public abstract void onError();

}
