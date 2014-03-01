package com.cliqconsulting.cclib.util;

import android.util.Log;

/**
 * CCLog
 * <p/>
 * Created by Flavio Ramos on 12/12/13 12:34 PM.
 * Copyright (c) 2013 Cliq Consulting. All rights reserved.
 */
public class CCLog {

	public static final String DEFAULT_TAG = "CCLib";
	public static boolean debugging = false;
	private static String mDefaultTag;

	public static void logError(String tag, String error) {
		if (debugging) {
			Log.e(tag, error);
		}
	}

	public static void logDebug(String tag, String message) {
		if (debugging) {
			Log.d(tag, message);
		}
	}

	public static void logError(String error) {
		logError(mDefaultTag, error);
	}

	public static void logDebug(String message) {
		Log.d(mDefaultTag, message);
	}

	public static void setDefaultTag(String defaultTag) {
		mDefaultTag = defaultTag;
	}
}
