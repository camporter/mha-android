package com.teamacra.myhomeaudio;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.format.Formatter;
import android.util.Log;

/**
 * An Application class extension that stores all the global information for our
 * application.
 * 
 * @author Cameron
 * 
 */
public class MHAApplication extends Application {

	public static final String TAG = "MyHomeAudio";
	public static final String PREFS_NAME = "MyHomeAudioPrefs";

	private boolean isLoggedIn;
	private String username;
	private String password;
	private String sessionId;

	
	@Override
	public void onCreate() {
		super.onCreate();
		this.isLoggedIn = false;

		SharedPreferences sharedPrefs = this.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
		// TODO: Do discovery instead of hardcoded value!
		prefsEditor.putString("hostAddress", "http://192.168.68.160:8080");
		prefsEditor.commit();
		
		Log.d(TAG, "Application created");
	}

	@Override
	public void onTerminate() {
		setLoggedOut();
		Log.d(TAG, "Application terminated");
		super.onTerminate();
	}
	
	/**
	 * Set the application state as logged in.
	 * @param username Username for the user logged in.
	 * @param password Password for the user logged in.
	 * @param sessionId Session assigned to the client for the user.
	 */
	public void setLoggedIn(String username, String password, String sessionId) {
		this.username = username;
		this.password = password;
		this.sessionId = sessionId;
		this.isLoggedIn = true;
	}
	
	/**
	 * Set the application state as logged out.
	 */
	public void setLoggedOut() {
		this.isLoggedIn = false;
		this.username = null;
		this.password = null;
		this.sessionId = null;
	}
	
	/**
	 * Is a user logged in?
	 * @return Whether a user is logged in.
	 */
	public boolean isLoggedIn() {
		return this.isLoggedIn;
	}

}
