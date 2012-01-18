package com.teamacra.myhomeaudio.http;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.SharedPreferences;

/**
 * Base class for all other HTTP action classes.
 * @author Cameron
 *
 */
public class HttpBase {
	protected String host;
	protected String localIPAddress;
	protected HttpClient httpClient;
	
	public HttpBase(SharedPreferences prefs) {
		this.host = prefs.getString("host", "");
		this.localIPAddress = prefs.getString("localIP", "");
		this.httpClient = new DefaultHttpClient();
	}
}
