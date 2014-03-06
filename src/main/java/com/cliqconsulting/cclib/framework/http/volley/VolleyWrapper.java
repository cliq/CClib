package com.cliqconsulting.cclib.framework.http.volley;

import android.content.Context;
import com.android.volley.*;
import com.android.volley.toolbox.Volley;
import com.cliqconsulting.cclib.framework.http.HttpResponse;
import com.cliqconsulting.cclib.framework.http.IHttpWrapper;
import com.cliqconsulting.cclib.framework.http.IHttpWrapperListener;

import java.util.HashMap;
import java.util.Map;

/**
 * VolleyWrapper
 * <p/>
 * Volley library abstraction.
 * <p/>
 * Created by Flavio Ramos on 2/19/14 18:22.
 * Copyright (c) 2013. All rights reserved.
 */
public class VolleyWrapper implements IHttpWrapper {

	private final Context mContext;
	private Map<Integer, VolleyRequestWrapper> mRequests = new HashMap<Integer, VolleyRequestWrapper>();
	private static RequestQueue mRequestQueue;

	public VolleyWrapper(Context context) {
		mContext = context;
		mRequestQueue = Volley.newRequestQueue(mContext);
	}

	@Override
	public int request(IHttpWrapperListener listener, Method method, String url, Map<String, String> values, Map<String, String> headers) {
		VolleyRequestWrapper requestWrapper = new VolleyRequestWrapper(listener, getVolleyMethod(method), url, values, this);

		if (headers != null) {
			requestWrapper.setHeaders(headers);
		}

		mRequests.put(listener.hashCode(), requestWrapper);

		mRequestQueue.add(requestWrapper.getRequest());

		return requestWrapper.hashCode();
	}

	private int getVolleyMethod(Method method) {
		switch (method) {
			case GET:
				return Request.Method.GET;
			case POST:
				return Request.Method.POST;
			case PUT:
				return Request.Method.PUT;
			case DELETE:
				return Request.Method.GET;
			case HEAD:
				return Request.Method.HEAD;
			case OPTIONS:
				return Request.Method.OPTIONS;
			case TRACE:
				return Request.Method.TRACE;
			case PATCH:
				return Request.Method.PATCH;
			default:
				return Request.Method.DEPRECATED_GET_OR_POST;
		}
	}

	@Override
	public void cancelRequest(int requestId) {
		mRequests.get(requestId).cancel();
		remove(requestId);
	}

	@Override public void remove(int requestId) {
		mRequests.remove(requestId);
	}

}
