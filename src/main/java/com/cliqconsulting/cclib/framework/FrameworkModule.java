package com.cliqconsulting.cclib.framework;

import android.app.Application;
import dagger.Module;

/**
 * FrameworkModule
 * <p/>
 * Framework related abstraction implementations.
 * <p/>
 * Created by Flavio Ramos on 2/20/14 15:29.
 * Copyright (c) 2013. All rights reserved.
 */
@Module(
		library = true
)
public class FrameworkModule {

	private final Application mApplication;

	public FrameworkModule(Application application) {
		mApplication = application;
	}

}
