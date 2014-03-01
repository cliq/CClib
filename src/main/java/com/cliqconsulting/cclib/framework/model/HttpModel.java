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
	@Override public final void load() {

		Map<String, String> requestData = getRequestData();
		Map<String, String> headersData = getHeaders();

		Iterator<String> iterator;
		String key;

		CCLog.logDebug("URL: " + getUrl());
		CCLog.logDebug("-   Sending " + getRequestMethod() + " request: ");
		CCLog.logDebug("-   Headers: ");
		iterator = headersData.keySet().iterator();
		while (iterator.hasNext()) {
			key = iterator.next();
			CCLog.logDebug("-       " + key + ": " + headersData.get(key));
		}
		CCLog.logDebug("-   Data: ");
		iterator = requestData.keySet().iterator();
		while (iterator.hasNext()) {
			key = iterator.next();
			CCLog.logDebug("-       " + key + ": " + requestData.get(key));
		}


		setLoadingStatus();
		getHttpWrapper().request(
				this,
				getRequestMethod(),
				getUrl(),
				requestData,
				headersData
		);
	}

	@Override public void onResponse(HttpResponse response) {
		if (response.getStatusCode() >= 200 && response.getStatusCode() < 400) {
			setContent(response.getData());
		} else {
			setError(String.valueOf(response.getStatusCode()));
		}

	}

	@Override public void onError(String error) {
		setError(error);
	}
}
