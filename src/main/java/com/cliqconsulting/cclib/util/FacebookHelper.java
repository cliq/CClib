package com.cliqconsulting.cclib.util;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.model.GraphUser;

/**
 * FacebookHelper
 * <p/>
 * Created by Flavio Ramos on 2/28/14 13:35.
 * Copyright (c) 2013. All rights reserved.
 */
public class FacebookHelper {

	public static void getSessionInfo(final CCSimpleHandler<GraphUser> handler) {
		Request.executeMeRequestAsync(Session.getActiveSession(), new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, com.facebook.Response response) {
				if (response.getError() == null) {
					handler.setSuccess(user);
				} else {
					handler.setError();
				}
			}
		});

	}
}
