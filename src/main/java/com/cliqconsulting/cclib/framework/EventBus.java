package com.cliqconsulting.cclib.framework;

import com.squareup.otto.Bus;

/**
 * EventBus
 * <p/>
 * Created by Flavio Ramos on 2/25/14 00:08.
 * Copyright (c) 2013. All rights reserved.
 */
public class EventBus extends Bus {

	private static EventBus mInstance;

	public static EventBus getInstance() {
		if (mInstance == null) {
			mInstance = new EventBus();
		}

		return mInstance;
	}

}
