package com.cliqconsulting.cclib.util;

import android.text.TextUtils;
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

	public static void logErrorWithTag(String tag, String... error) {
		if (debugging) {
			Log.e(tag, TextUtils.join(" ", error));
		}
	}

	public static void logDebugWithTag(String tag, String... message) {
		if (debugging) {
			Log.d(tag, TextUtils.join(" ", message));
		}
	}

	public static void logInfoWithTag(String tag, String... message) {
		if (debugging) {
			Log.i(tag, TextUtils.join(" ", message));
		}
	}

	public static void logWarnWithTag(String tag, String... message) {
		if (debugging) {
			Log.w(tag, TextUtils.join(" ", message));
		}
	}

	public static void logError(String... error) {
		if (debugging) {
			Log.e(mDefaultTag, TextUtils.join(" ", error));
		}
	}

	public static void logDebug(String... message) {
		if (debugging) {
			Log.d(mDefaultTag, TextUtils.join(" ", message));
		}
	}

	public static void logInfo(String... message) {
		if (debugging) {
			Log.i(mDefaultTag, TextUtils.join(" ", message));
		}
	}

	public static void logWarn(String... message) {
		if (debugging) {
			Log.w(mDefaultTag, TextUtils.join(" ", message));
		}
	}

	public static void setDefaultTag(String defaultTag) {
		mDefaultTag = defaultTag;
	}
}
