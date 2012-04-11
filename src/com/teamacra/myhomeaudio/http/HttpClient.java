package com.teamacra.myhomeaudio.http;

import org.json.JSONException;
import org.json.JSONObject;

import com.teamacra.myhomeaudio.MHAApplication;

import android.content.SharedPreferences;

public class HttpClient extends HttpBase {

	public HttpClient(MHAApplication app) {
		super(app);
	}

	/**
	 * Sends a login request to the server using the information given.
	 * 
	 * @param username
	 * @param password
	 * @return The sessionID for the user and initialConfig status. Returns null if the login failed.
	 */
	public String[] login(String username, String password) {
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("username", username);
			requestObject.put("password", password);
			requestObject.put("ipaddress", app.getLocalAddress());
			requestObject.put("macaddress", app.getMacAddress());
			requestObject.put("bluetoothname", app.getBluetoothName());
			JSONObject responseObject = executePostRequest("/client/login", requestObject);
			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK) {
				String[] responseData = {responseObject.getString("session"),
							responseObject.getString("initialConfig")};
				return responseData;
			}	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Sends a user register request to the server using the given information.
	 * @param username
	 * @param password
	 * @return The status code from the server indicating the result.
	 */
	public int register(String username, String password) {
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("username", username);
			requestObject.put("password", password);
			return executeSimplePostRequest("/user/register", requestObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return StatusCode.STATUS_FAILED;
	}
	
	/**
	 * Sends a user logout request to the server using the given information.
	 * @param sessionId The session to log out of.
	 * @return The status code from the server indicating the result.
	 */
	public int logout(String sessionId) {
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", sessionId);
			return executeSimplePostRequest("/client/logout", requestObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return StatusCode.STATUS_FAILED;
	}
}
