package com.cliqconsulting.cclib.framework;

import com.cliqconsulting.cclib.framework.model.ISubscriberView;
import com.cliqconsulting.cclib.framework.model.Model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Missing Link
 * com.cliqconsulting.cclib.framework.DataManager
 * <p/>
 * Created by Flavio Ramos on 2/19/14 12:27.
 * Copyright (c) 2013 Fanatee. All rights reserved.
 */
public class DataManager {

	private static Map<Class<? extends Model>, Model> mModels = new HashMap<Class<? extends Model>, Model>();
	private static Map<ISubscriberView, Class<? extends Model>> mSubscribers = new HashMap<ISubscriberView, Class<? extends Model>>();
	private static DataManager mInstance;

	public DataManager() {
		mInstance = this;
	}

	public static void subscribeView(Model model, ISubscriberView subscriberView) {
		mSubscribers.put(subscriberView, model.getClass());
		model.onSubscribed(subscriberView);
	}

	public static void unsubscribeView(ISubscriberView subscriberView) {
		mSubscribers.remove(subscriberView);
	}

	public static void registerModel(Model model) {
		mModels.put(model.getClass(), model);
	}

	public static void notifySubscribers(Model model, Model.Status status) {
		Iterator<ISubscriberView> it = mSubscribers.keySet().iterator();

		ISubscriberView s;

		while (it.hasNext()) {
			s = it.next();
			Class<? extends Model> currentModel = mSubscribers.get(s);

			if (currentModel.equals(model.getClass())) {
				s.onModelStatus(status);
			}
		}
	}

}
