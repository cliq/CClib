package com.cliqconsulting.cclib.framework;

import android.app.Application;
import com.cliqconsulting.cclib.framework.http.IHttpWrapper;

import java.util.ArrayList;

/**
 * BaseApplication
 * <p/>
 * Created by Flavio Ramos on 2/19/14 10:28.
 * Copyright (c) 2013. All rights reserved.
 */
public abstract class BaseApplication extends Application {

	public static boolean isRunning = false;
	protected static int mNotificationsId = 1;

	@Override
	public void onCreate() {
		super.onCreate();

		ArrayList<Object> modules = getModules();
		modules.add(0, new FrameworkModule(this));

		Injector.init(modules.toArray());
	}

	protected abstract ArrayList<Object> getModules();

	public static int getNotificationsId() {
		return mNotificationsId;
	};
}