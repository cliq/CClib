package com.cliqconsulting.cclib.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Missing Link
 * com.cliqconsulting.cclib.util.CCFontHelper
 * <p/>
 * Created by Flavio Ramos on 1/16/14 10:58.
 * Copyright (c) 2013 Cliq Consulting. All rights reserved.
 */
public class CCFontHelper {

	private static Map<String, Typeface> fonts = new HashMap<String, Typeface>();
	private static String defaultFontPath;

	public final static void overrideFonts(final Context context, final View v, String fontPath) {

		if (fonts.get(fontPath) == null) {
			fonts.put(fontPath, Typeface.createFromAsset(context.getAssets(), fontPath));
		}

		try {
			if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0; i < vg.getChildCount(); i++) {
					View child = vg.getChildAt(i);
					overrideFonts(context, child, fontPath);
				}
			} else if (v instanceof TextView || v instanceof EditText) {
				((TextView) v).setTypeface(fonts.get(fontPath));
			}
		} catch (Exception e) {
		}
	}

	public final static void overrideFonts(final Context context, final View v) {

		try {
			if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0; i < vg.getChildCount(); i++) {
					View child = vg.getChildAt(i);
					overrideFonts(context, child, defaultFontPath);
				}
			} else if (v instanceof TextView || v instanceof EditText) {
				((TextView) v).setTypeface(fonts.get(defaultFontPath));
			}
		} catch (Exception e) {
		}
	}

	public final static void setDefaultFontPath(String fontPath) {
		defaultFontPath = fontPath;
	}
}
