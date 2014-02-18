package com.cliqconsulting.cclib.util;

import android.util.Log;

 /**
 * Missing Link
 * com.cliqconsulting.cclib.facebook.CCLog
 * 
 * Created by Flavio Ramos on 12/12/13 12:34 PM.
 * Copyright (c) 2013 Cliq Consulting. All rights reserved.
 */
public class CCLog {

	public static final String DEFAULT_TAG = "CCLib";
	public static boolean debugging = false;

	public static void logError(String tag, String error) {
		if (debugging) {
			Log.e(tag, error);
		}
	}

	public static void logDebug(String tag, String error) {
		if (debugging) {
			Log.d(tag, error);
		}
	}

}
