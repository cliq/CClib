package com.cliqconsulting.cclib.framework.persistence;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * AndroidPreferencesPeristence
 * <p/>
 * Save data on application' preferences.
 * This method will save EVERYTHING as String.
 * <p/>
 * Created by Flavio Ramos on 2/20/14 16:12.
 * Copyright (c) 2013. All rights reserved.
 */
public class AndroidPreferencesPeristence implements IPersistenceMethod {

	private final Context mContext;
	private final SharedPreferences mSharedPreferences;

	public AndroidPreferencesPeristence(Context context) {
		mContext = context;
		mSharedPreferences = context.getSharedPreferences(mContext.getApplicationInfo().packageName, 0);
	}

	@Override
	public boolean saveBytes(String key, byte[] value) {
		return false;
	}

	@Override public boolean saveString(String key, String value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	@Override public boolean saveInt(String key, int value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putInt(key, value);
		return editor.commit();
	}

	@Override public boolean saveLong(String key, long value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putLong(key, value);
		return editor.commit();
	}

	@Override public boolean saveFloat(String key, float value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putFloat(key, value);
		return editor.commit();
	}

	@Override public boolean saveBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}

	@Override
	public byte[] loadBytes(String key) {
		return null;
	}

	@Override public String loadString(String key) {
		String data = mSharedPreferences.getString(key, null);
		return data;
	}

	@Override public int loadInt(String key) {
		int data = mSharedPreferences.getInt(key, 0);
		return data;
	}

	@Override public long loadLong(String key) {
		long data = mSharedPreferences.getLong(key, 0);
		return data;
	}

	@Override public float loadFloat(String key) {
		float data = mSharedPreferences.getFloat(key, 0);
		return data;
	}

	@Override public boolean loadBoolean(String key) {
		boolean data = mSharedPreferences.getBoolean(key, false);
		return data;
	}

	@Override
	public boolean clear(String key) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(key, null);
		return editor.commit();
	}
}
