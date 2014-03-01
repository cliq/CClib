package com.cliqconsulting.cclib.framework.http;

import android.content.Context;
import android.util.SparseArray;
import com.android.volley.*;
import com.android.volley.toolbox.Volley;

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
	private static SparseArray<VolleyRequestWrapper> mRequests = new SparseArray<VolleyRequestWrapper>();
	private static RequestQueue mRequestQueue;
	private IHttpWrapperListener mListener;
	private Map<String, String> mValues;

	public VolleyWrapper(Context context) {
		mContext = context;
		mRequestQueue = Volley.newRequestQueue(mContext);
	}

	@Override
	public int request(IHttpWrapperListener listener, Method method, String url, Map<String, String> values, Map<String, String> headers) {
		mListener = listener;
		mValues = values;

		VolleyRequestWrapper requestWrapper = new VolleyRequestWrapper(getVolleyMethod(method), url, this);

		if (headers != null) {
			requestWrapper.setHeaders(headers);
		}

		mRequests.put(requestWrapper.hashCode(), requestWrapper);

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
	public void cancelRequest(int requestHash) {
		mRequests.get(requestHash).cancel();
		mRequests.remove(requestHash);
	}

	/**
	 * Wrapper to avoid Volley' scope problems (can' get request hash from ErrorListeners)
	 */
	class VolleyRequestWrapper implements Response.ErrorListener {

		private final Request<byte[]> mRequest;

		public VolleyRequestWrapper(int method, String url, VolleyWrapper volleyWrapper) {

			mRequest = new Request<byte[]>(method, url, this) {
				public int statusCode;
				public Map<String, String> headers;

				@Override
				protected Response<byte[]> parseNetworkResponse(NetworkResponse networkResponse) {
					statusCode = networkResponse.statusCode;
					headers = networkResponse.headers;
					return Response.success(networkResponse.data, getCacheEntry());
				}

				@Override
				protected void deliverResponse(byte[] bytes) {
					mRequests.remove(hashCode());
					mListener.onResponse(new HttpResponse(statusCode, headers, bytes));
				}

				@Override protected Map<String, String> getParams() throws AuthFailureError {
					return mValues;
				}
			};

			// TODO set this configurable externally
			mRequest.setRetryPolicy(
					new DefaultRetryPolicy(
							30000,
							DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
							DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
					)
			);
		}

		@Override
		public void onErrorResponse(VolleyError volleyError) {
			mRequests.remove(hashCode());

			if (volleyError.networkResponse != null) {
				mListener.onResponse(new HttpResponse(volleyError.networkResponse.statusCode, volleyError.networkResponse.headers, null));
			} else {
				mListener.onError(volleyError.getMessage());
			}

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
	}
}
