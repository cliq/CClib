package com.cliqconsulting.cclib.facebook;

import android.content.Intent;
import android.os.Bundle;
import com.cliqconsulting.cclib.framework.BaseActivity;
import com.cliqconsulting.cclib.util.CCLog;
import com.facebook.Session;
import com.facebook.SessionState;

import java.util.List;

/**
 * Base class to do Facebook Connect procedure
 * <p/>
 * User: flavioramos
 * 12/11/13 2:30 PM
 */
public abstract class BaseFacebookActivity extends BaseActivity {

	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private boolean facebookConnected;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Session session = Session.getActiveSession();

		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				facebookConnected = true;
			} else {
				facebookConnected = false;
			}
		} else {
			facebookConnected = false;
		}
	}

	public void startFacebookConnect() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			openSession(session);
		} else {
			Session.openActiveSession(this, true, statusCallback);
		}
	}

	private void openSession(Session session) {
		Session.OpenRequest openRequest = new Session.OpenRequest(this);
		openRequest.setCallback(statusCallback);
		openRequest.setPermissions(getPermissionList());
		session.openForPublish(openRequest);
	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	private void checkFacebookPermissions() {
		List<String> requested = getPermissionList();
		List<String> obtained = Session.getActiveSession().getPermissions();

		CCLog.logDebug(CCLog.DEFAULT_TAG, "FB Permissions asked: " + requested.size() + ", obtained: " + obtained.size());

		if (requested.size() <= obtained.size()) {
			facebookConnectSuccess();
		} else {
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, getPermissionList());
			Session.getActiveSession().requestNewPublishPermissions(newPermissionsRequest);
		}

	}

	/**
	 * Must return a list of permissions needed on Facebook
	 *
	 * @return List    List of String, see FB documentation for reference
	 */
	abstract public List<String> getPermissionList();

	/**
	 * Successfull Facebook Connection handler
	 */
	abstract public void facebookConnectSuccess();

	/**
	 * Failed Facebook Connection handler
	 */
	abstract public void facebookConnectFailure();

	public boolean isFacebookConnected() {
		return facebookConnected;
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state.isOpened()) {
				Session.setActiveSession(session);
				checkFacebookPermissions();
			} else {
				facebookConnectFailure();
			}
		}
	}

}
