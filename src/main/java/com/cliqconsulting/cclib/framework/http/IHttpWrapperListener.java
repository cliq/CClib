package com.cliqconsulting.cclib.framework.http;

/**
 * IHttpWrapperListener
 * <p/>
 * Created by Flavio Ramos on 2/25/14 01:24.
 * Copyright (c) 2013. All rights reserved.
 */
public interface IHttpWrapperListener {

	public void onResponse(HttpResponse response);

	public void onError(String error);

}
