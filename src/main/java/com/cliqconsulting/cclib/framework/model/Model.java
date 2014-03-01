package com.cliqconsulting.cclib.framework.model;

import com.cliqconsulting.cclib.framework.DataManager;

/**
 * Model
 * <p/>
 * Created by Flavio Ramos on 2/19/14 11:45.
 * Copyright (c) 2013. All rights reserved.
 */
public abstract class Model<T> {

	private Object mCurrentError;
	private Status mCurrentStatus;
	private T mContent;

	public Model() {
		mCurrentStatus = Status.EMPTY;
		DataManager.registerModel(this);
	}

	public static enum Status {
		EMPTY("EMPTY"),
		LOADED("LOADED"),
		OUTDATED("OUTDATED"),
		LOADING("LOADING"),
		ERROR("ERROR");

		private final String mString;

		Status(String toString) {
			mString = toString;
		}

		@Override public String toString() {
			return mString;
		}
	}

	protected final void setContent(T content) {
		mContent = content;
		mCurrentStatus = Status.LOADED;
		mCurrentError = null;
		onStatusChanged();
	}

	protected final void clearContent() {
		mContent = null;
		mCurrentStatus = Status.EMPTY;
		mCurrentError = null;
		onStatusChanged();
	}

	protected final void setError(String error) {
		mContent = null;
		mCurrentStatus = Status.ERROR;
		mCurrentError = error;
		onStatusChanged();
	}

	protected void setLoadingStatus() {
		mContent = null;
		mCurrentStatus = Status.LOADING;
		mCurrentError = null;
		onStatusChanged();
	}

	public abstract void load();

	public abstract void onStatusChanged();

	public abstract void onSubscribed(ISubscriberView subscriberView);

	public final Object getError() {
		return mCurrentError;
	}

	public final T getContent() {
		return mContent;
	}

	public final Status getCurrentStatus() {
		return mCurrentStatus;
	}

}
