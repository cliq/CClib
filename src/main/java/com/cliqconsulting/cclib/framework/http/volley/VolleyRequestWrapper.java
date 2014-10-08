package com.cliqconsulting.cclib.framework.http.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cliqconsulting.cclib.framework.http.HttpResponse;
import com.cliqconsulting.cclib.framework.http.IHttpWrapperListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * VolleyRequestWrapper
 * <p/>
 * Wrapper to avoid Volley's scope problems (can't get request hash from ErrorListeners)
 * <p/>
 * Created by Flavio Ramos on 3/3/14 18:28.
 * Copyright (c) 2013. All rights reserved.
 */
public class VolleyRequestWrapper implements Response.ErrorListener {

	private final VolleyRequest mRequest;
	private final IHttpWrapperListener mListener;
	private final Map<String, String> mValues;
	private final VolleyWrapper mVolleyWrapper;

	public VolleyRequestWrapper(IHttpWrapperListener listener, int method, String url, Map<String, String> values, VolleyWrapper volleyWrapper) {
		mListener = listener;
		mValues = values;
		mVolleyWrapper = volleyWrapper;

		// when using GET method getParams() is never called, so append parameters to URL manually
		if (method == VolleyRequest.Method.GET && values.size() > 0) {
			StringBuilder sb = new StringBuilder(url);
			sb.append("?");

			for(HashMap.Entry<String, String> e : mValues.entrySet()){
				if(sb.length() > 0){
					sb.append('&');
				}
				try {
					sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}

			url = sb.toString();
		}

		mRequest = new VolleyRequest(method, url, this);

		// TODO set this configurable externally
		mRequest.setRetryPolicy(
				new DefaultRetryPolicy(
						60000,
						0,
						DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
				)
		);
	}

	@Override
	public void onErrorResponse(VolleyError volleyError) {
		mVolleyWrapper.remove(hashCode());
		mListener.onError(volleyError.getLocalizedMessage());
	}

	public void setHeaders(Map<String, String> headers) {
		try {
			mRequest.getHeaders().putAll(headers);
		} catch (AuthFailureError authFailureError) {
			authFailureError.printStackTrace();
		}
	}

	public void cancel() {
		mRequest.cancel();
	}

	public Request<byte[]> getRequest() {
		return mRequest;
	}

	class VolleyRequest extends Request<byte[]> {

		public int statusCode;
		public Map<String, String> headers;

		public VolleyRequest(int method, String url, Response.ErrorListener listener) {
			super(method, url, listener);
		}

		@Override
		protected Response<byte[]> parseNetworkResponse(NetworkResponse networkResponse) {
			statusCode = networkResponse.statusCode;
			headers = networkResponse.headers;
			return Response.success(networkResponse.data, getCacheEntry());
		}

		@Override
		protected void deliverResponse(byte[] bytes) {
			mListener.onResponse(new HttpResponse(statusCode, headers, bytes));
		}

		@Override protected Map<String, String> getParams() throws AuthFailureError {
			return mValues;
		}
	}

}
