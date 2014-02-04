package com.cliqconsulting.cclib.util;

import java.text.Normalizer;

/**
 * Missing Link
 * com.cliqconsulting.cclib.util.CCUtil
 * <p/>
 * Created by Flavio Ramos on 1/17/14 13:36.
 * Copyright (c) 2013 Fanatee. All rights reserved.
 */
public class CCUtil {

	public static String removeAccents(String notNullSource) {
		String normalized = Normalizer.normalize(notNullSource, Normalizer.Form.NFD);
		normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		return normalized;

	}

}
