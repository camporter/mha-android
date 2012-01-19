package com.teamacra.myhomeaudio.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;

/**
 * Base class for all other HTTP action classes.
 * 
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

	/**
	 * Executes a request to the server that returns the response object.
	 * 
	 * @param apiUrl
	 *            The portion of the URL indicating which part of the server's
	 *            API to send to.
	 * @param jsonRequestData
	 *            The data to send to the server in JSON form.
	 * @return The response in JSON form. Null if we don't get an object or something
	 *         bad happens.
	 */
	protected JSONObject executePostRequest(String apiUrl, JSONObject jsonRequestData) {
		try {
			HttpPost httpPost = new HttpPost(host + apiUrl);

			httpPost.setEntity(new StringEntity(jsonRequestData.toString()));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			try {
				String response = this.httpClient.execute(httpPost, responseHandler);
				JSONObject responseObject = new JSONObject(response);
				return responseObject;
			} catch (ClientProtocolException e) {
				// TODO: Do something with these possibilities later
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Executes a request to the server that has a simple single-status
	 * response.
	 * 
	 * @param apiUrl
	 *            The portion of the URL indicating which part of the server's
	 *            API to send to.
	 * @param jsonRequestData
	 *            The data to send to the server in JSON form.
	 * @return The status code returned from the server.
	 */
	protected int executeSimplePostRequest(String apiUrl, JSONObject jsonRequestData) {
		JSONObject responseObject = executePostRequest(apiUrl, jsonRequestData);
		if (responseObject != null) {
			try {
				return responseObject.getInt("status");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return StatusCode.STATUS_FAILED;
	}
}
