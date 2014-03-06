package com.cliqconsulting.cclib.framework.model;

/**
 * ModelEvent
 * <p/>
 * Created by Flavio Ramos on 3/5/14 16:27.
 * Copyright (c) 2013. All rights reserved.
 */
public abstract class ModelEvent<T extends Model> {

	private final T mModel;
	private final Model.Status mStatus;

	public ModelEvent(T model, Model.Status status) {
		mModel = model;
		mStatus = status;
	}

	public T getModel() {
		return mModel;
	}

	public Model.Status getStatus() {
		return mStatus;
	}

}
