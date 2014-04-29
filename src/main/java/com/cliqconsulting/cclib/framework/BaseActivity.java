package com.cliqconsulting.cclib.framework;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

/**
 * BaseActivity
 * <p/>
 * Created by Flavio Ramos on 2/20/14 14:14.
 * Copyright (c) 2013. All rights reserved.
 */
public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override protected void onPause() {
		super.onPause();
		BaseApplication.isRunning = false;
	}

	@Override protected void onResume() {
		super.onResume();
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(BaseApplication.getNotificationsId());
		BaseApplication.isRunning = true;
	}
}
