package com.teamacra.myhomeaudio.http;

import android.content.SharedPreferences;

/**
 * Base class for all other HTTP action classes.
 * @author Cameron
 *
 */
public class HttpBase {
	protected String host;
	protected String localIPAddress;
	
	public HttpBase(SharedPreferences prefs) {
		this.host = prefs.getString("host", "");
		this.localIPAddress = prefs.getString("localIP", "");
	}
}
