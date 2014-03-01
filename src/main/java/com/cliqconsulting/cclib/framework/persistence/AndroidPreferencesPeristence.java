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
	public boolean save(String key, byte[] value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(key, new String(value));
		return editor.commit();
	}

	@Override
	public byte[] load(String key) {
		String data = mSharedPreferences.getString(key, null);
		if (data != null) {
			return data.getBytes();
		} else {
			return new byte[0];
		}
	}

	@Override
	public boolean clear(String key) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(key, null);
		return editor.commit();
	}
}
