package com.cliqconsulting.cclib.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Missing Link
 * com.cliqconsulting.cclib.util.GcmUtil
 * <p/>
 * Helps with Google Cloud Messaging calls.
 * <p/>
 * Created by Flavio Ramos on 1/3/14 17:36.
 * Copyright (c) 2013 Fanatee. All rights reserved.
 */
public class GcmUtil {

	private String mProjectId;
	private Context mContext;
	private CCSimpleHandler mHandler;

	/**
	 *
	 * @param projectId	Project Id from Google API Console
	 * @param context
	 */
	public GcmUtil(String projectId, Context context) {
		mProjectId = projectId;
		mContext = context;
	}

	private void setRegistrationId(String msg) {
		if (msg != null) {
			mHandler.setSuccess(msg);
		} else {
			mHandler.onError();
		}
	}

	public void register(CCSimpleHandler handler) {
		mHandler = handler;

		new AsyncTask<Object, String, String>() {
			@Override
			protected String doInBackground(Object... params) {
				String registrationId = null;

				try {
					GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
					registrationId = gcm.register(mProjectId);
				} catch (IOException e) {
					e.printStackTrace();
				}

				return registrationId;
			}

			@Override
			protected void onPostExecute(String msg) {
				setRegistrationId(msg);
			}
		}.execute(null, null, null);

	}

}
