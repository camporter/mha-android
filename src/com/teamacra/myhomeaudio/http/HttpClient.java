package com.teamacra.myhomeaudio.http;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;

public class HttpClient extends HttpBase {

	public HttpClient(SharedPreferences prefs) {
		super(prefs);
	}

	/**
	 * Sends a login request to the server using the information given.
	 * 
	 * @param username
	 * @param password
	 * @param ipAddress
	 * @param macAddress
	 * @param bluetoothName
	 * @return The sessionID for the user. Returns null if the login failed.
	 */
	public String login(String username, String password, String ipAddress, String macAddress,
			String bluetoothName) {
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("username", username);
			requestObject.put("password", password);
			requestObject.put("ipaddress", ipAddress);
			requestObject.put("macaddress", macAddress);
			requestObject.put("bluetoothname", bluetoothName);
			JSONObject responseObject = executePostRequest("/client/login", requestObject);
			return responseObject.getString("session");
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
			return executeSimplePostRequest("/client/register", requestObject);
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
