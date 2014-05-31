package com.cliqconsulting.cclib.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.*;

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

	/**
	 * Saves the logcat data into a text file, at the Downloads folder. The location has to be public, so
	 * other apps are able to access. You should delete the file when it' not needed since it may contain sensitive
	 * information, use at your own risk.
	 *
	 * @param context
	 * @param handler Handles the resulting file.
	 */
	public static void dumpLogcat(final Context context, final CCSimpleHandler<File> handler) {
		new AsyncTask<Object, Object, File>() {
			@Override protected File doInBackground(Object... objects) {
				StringBuilder log = null;

				try {
					Process process = Runtime.getRuntime().exec("logcat -d");
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

					log = new StringBuilder();
					String line = "";
					while ((line = bufferedReader.readLine()) != null) {
						log.append(line + "\r\n");
					}
				} catch (Exception e) {
					CCLog.logError(e.toString());
				}

				File file = null;

				try {
					file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "log" + String.valueOf(System.currentTimeMillis()) + ".txt");
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(log.toString().getBytes());
					fos.close();
				} catch (IOException e) {
					CCLog.logError(e.toString());
				}

				return file;
			}

			@Override protected void onPostExecute(File f) {
				if (f != null) handler.setSuccess(f);
				else handler.setError(null);
			}
		}.execute();
	}

}
