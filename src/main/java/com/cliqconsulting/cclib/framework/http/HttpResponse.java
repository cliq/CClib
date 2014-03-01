package com.cliqconsulting.cclib.framework.http;

import java.util.Map;

/**
 * HttpResponse
 * <p/>
 * Created by Flavio Ramos on 2/20/14 19:36.
 * Copyright (c) 2013. All rights reserved.
 */
public class HttpResponse {

	private final Map<String, String> mHeaders;
	private byte[] mData;
	private int mStatusCode;

	public HttpResponse(int statusCode, Map<String, String> headers, byte[] data) {
		mStatusCode = statusCode;
		mHeaders = headers;
		mData = data;
	}

	public int getStatusCode() {
		return mStatusCode;
	}

	public Map<String, String> getHeaders() {
		return mHeaders;
	}

	public byte[] getData() {
		return mData;
	}

}
