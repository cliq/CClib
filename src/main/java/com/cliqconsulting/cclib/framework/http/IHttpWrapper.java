package com.cliqconsulting.cclib.framework.http;

import java.util.Map;

/**
 * IHttpWrapper
 * <p/>
 * Created by Flavio Ramos on 2/19/14 19:44.
 * Copyright (c) 2013. All rights reserved.
 */
public interface IHttpWrapper {

	public enum Method {
		GET,
		POST,
		PUT,
		DELETE,
		HEAD,
		OPTIONS,
		TRACE,
		PATCH
	}

	public int request(IHttpWrapperListener listener, Method method, String url, Map<String, String> values, Map<String, String> headers);

	public void cancelRequest(int requestId);

}
