package com.cliqconsulting.cclib.framework.model;

import com.cliqconsulting.cclib.framework.EventBus;

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
		postEvent();
	}

	public final void clearContent() {
		if (mCurrentStatus == Status.EMPTY) return;
		mContent = null;
		mCurrentStatus = Status.EMPTY;
		mCurrentError = null;
		postEvent();
	}

	public final void setOutdated() {
		if (mCurrentStatus == Status.OUTDATED) return;
		mCurrentStatus = Status.OUTDATED;
		mCurrentError = null;
		postEvent();
	}

	protected final void setError(String error) {
		mContent = null;
		mCurrentStatus = Status.ERROR;
		mCurrentError = error;
		postEvent();
	}

	protected void setLoadingStatus() {
		if (mCurrentStatus == Status.LOADING) return;
		mContent = null;
		mCurrentStatus = Status.LOADING;
		mCurrentError = null;
		postEvent();
	}

	public void postEvent() {
		EventBus.getInstance().post(produceEvent());
	}

	public abstract ModelEvent produceEvent();

	public abstract void load();

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
