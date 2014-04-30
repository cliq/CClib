package com.cliqconsulting.cclib.google;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import com.cliqconsulting.cclib.util.CCSimpleHandler;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * GcmUtil
 * <p/>
 * Helps with Google Cloud Messaging calls.
 * <p/>
 * Created by Flavio Ramos on 1/3/14 17:36.
 * Copyright (c) 2013 Cliq Consulting. All rights reserved.
 */
public class GcmUtil {

	public static final String KEY_REGISTRATION_ID = "registrationId";
	public static final String KEY_APP_VERSION = "appVersion";
	public static final String PREF_GCM_REGISTRATION = "gcm_registration";
	private String mProjectId;
	private Context mContext;
	private CCSimpleHandler mHandler;

	/**
	 * @param projectId Project Id from Google API Console
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
			mHandler.onError(null);
		}
	}

	public void register(CCSimpleHandler handler) {
		mHandler = handler;

		String currentRegistrationId = getRegistrationId();

		if (currentRegistrationId == null) {
			new AsyncTask<Object, String, String>() {
				@Override
				protected String doInBackground(Object... params) {
					String registrationId = null;

					try {
						GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
						registrationId = gcm.register(mProjectId);
						storeRegistrationId(registrationId);
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
		} else {
			setRegistrationId(currentRegistrationId);
		}
	}

	private String getRegistrationId() {
		final SharedPreferences prefs = mContext.getSharedPreferences(PREF_GCM_REGISTRATION, Context.MODE_PRIVATE);
		String registrationId = prefs.getString(KEY_REGISTRATION_ID, null);

		if (registrationId == null) {
			return null;
		}

		int registeredVersion = prefs.getInt(KEY_APP_VERSION, 0);
		int currentVersion = getAppVersion();
		if (registeredVersion != currentVersion) {
			return null;
		}
		return registrationId;
	}

	private int getAppVersion() {
		try {
			PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// should never happen
			return 0;
		}
	}

	private void storeRegistrationId(String regId) {
		final SharedPreferences prefs = mContext.getSharedPreferences(PREF_GCM_REGISTRATION, Context.MODE_PRIVATE);
		int appVersion = getAppVersion();
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(KEY_REGISTRATION_ID, regId);
		editor.putInt(KEY_APP_VERSION, appVersion);
		editor.commit();
	}

}
