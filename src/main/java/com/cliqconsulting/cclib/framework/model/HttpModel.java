package com.cliqconsulting.cclib.framework.model;

import com.cliqconsulting.cclib.framework.http.HttpResponse;
import com.cliqconsulting.cclib.framework.http.IHttpWrapper;
import com.cliqconsulting.cclib.framework.http.IHttpWrapperListener;
import com.cliqconsulting.cclib.util.CCLog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * HttpModel
 * <p/>
 * Created by Flavio Ramos on 2/19/14 18:17.
 * Copyright (c) 2013. All rights reserved.
 */
public abstract class HttpModel extends Model<byte[]> implements IHttpWrapperListener {

	protected abstract Map<String, String> getRequestData();

	protected abstract String getUrl();

	protected abstract IHttpWrapper.Method getRequestMethod();

	protected abstract IHttpWrapper getHttpWrapper();

	/**
	 * Empty headers. Override this if you want to set.
	 *
	 * @return
	 */
	protected Map<String, String> getHeaders() {
		return new HashMap<String, String>();
	}

	/**
	 * Perform http request.
	 */
	@Override public void load() {
		if (getCurrentStatus().equals(Status.LOADING)) {
			return;
		}

		Map<String, String> requestData = getRequestData();
		Map<String, String> headersData = getHeaders();

		Iterator<String> iterator;
		String key;

		CCLog.logDebug("[HTTP] URL:", getUrl());
		CCLog.logDebug("[HTTP]   Request method:", getRequestMethod().toString());
		CCLog.logDebug("[HTTP]   Headers:");
		iterator = headersData.keySet().iterator();
		while (iterator.hasNext()) {
			key = iterator.next();
			CCLog.logDebug("[HTTP]       ", key, ":", headersData.get(key));
		}
		CCLog.logDebug("[HTTP]   Data: ");
		iterator = requestData.keySet().iterator();
		while (iterator.hasNext()) {
			key = iterator.next();
			CCLog.logDebug("[HTTP]       ", key, ":", requestData.get(key));
		}

		setLoadingStatus();

		getHttpWrapper().request(this, getRequestMethod(), getUrl(), requestData, headersData);
	}

	@Override public void onResponse(HttpResponse response) {
		CCLog.logDebug("[HTTP] Received from URL:", getUrl(), "with status", String.valueOf(response.getStatusCode()));
		CCLog.logDebug(new String(response.getData()));

		if (response.getStatusCode() >= 200 && response.getStatusCode() < 400) {
			setContent(response.getData());
		} else {
			setError(String.valueOf(response.getStatusCode()));
		}
	}

	@Override public void onError(String error) {
		CCLog.logError("[ERROR]", error);
		setError(error);
	}
}
