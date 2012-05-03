package com.teamacra.myhomeaudio.http;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teamacra.myhomeaudio.MHAApplication;

public class HttpClient extends HttpBase {

	public HttpClient(MHAApplication app) {
		super(app);
	}

	/**
	 * Sends a login request to the server using the information given.
	 * 
	 * @param username
	 * @param password
	 * @return First element - The sessionID for the user, Second element - The
	 *         configured status. Returns an array with each element set to null
	 *         if the login failed.
	 */
	public String[] login(String username, String password) {
		JSONObject requestObject = new JSONObject();
		String[] responseData = { null, null };
		try {
			requestObject.put("username", username);
			requestObject.put("password", password);
			requestObject.put("ipaddress", app.getLocalAddress());
			requestObject.put("macaddress", app.getMacAddress());
			requestObject.put("bluetoothname", app.getBluetoothName());
			JSONObject responseObject = executePostRequest("/client/login",
					requestObject);
			if (responseObject != null
					&& responseObject.getInt("status") == StatusCode.STATUS_OK) {
				responseData[0] = responseObject.getString("session");
				responseData[1] = responseObject.getString("configured");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("Response:" + Arrays.toString(responseData));
		return responseData;
	}

	/**
	 * Sends a user register request to the server using the given information.
	 * 
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
	 * 
	 * @param sessionId
	 *            The session to log out of.
	 * @return The status code from the server indicating the result.
	 */
	public int logout() {
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", app.getSessionId());
			return executeSimplePostRequest("/client/logout", requestObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return StatusCode.STATUS_FAILED;
	}

	public int initialConfig(JSONArray signaturesAsJSON) {
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", app.getSessionId());
			requestObject.put("signatures", signaturesAsJSON);
			return executeSimplePostRequest("/client/initialconfig",
					requestObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return StatusCode.STATUS_FAILED;
	}

	public int location(JSONArray devices){
		JSONObject requestObject = new JSONObject();
		try{
			requestObject.put("session", app.getSessionId());
			requestObject.put("locations",devices);
			return executeSimplePostRequest("/client/locations",requestObject);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return StatusCode.STATUS_FAILED;
	}
	
}
