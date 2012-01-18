package com.teamacra.myhomeaudio.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;

public class HttpClient extends HttpBase {

	public HttpClient(SharedPreferences prefs) {
		super(prefs);
	}
	
	/**
	 * Sends a login request to the server using the information given.
	 * @param username
	 * @param password
	 * @param ipAddress
	 * @param macAddress
	 * @param bluetoothName
	 * @return Returns true if the login was successful, false if it wasn't.
	 */
	public boolean login(String username, String password, String ipAddress, String macAddress,
			String bluetoothName) {
		try {
			String url = this.host + "/client/login";

			HttpPost httpPost = new HttpPost(url);

			httpPost.setEntity(new StringEntity("{" + "\"username\":\"" + username + "\","
					+ "\"password\":\"" + password + "\"," + "\"ipaddress\":\"" + ipAddress + "\","
					+ "\"macaddress\":\"" + macAddress + "\"," + "\"bluetoothname\":\""
					+ bluetoothName + "\"" + "}"));

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = this.httpClient.execute(httpPost, responseHandler);
			try {
				JSONObject responseObject = new JSONObject(response);
				if (responseObject.getInt("status") == StatusCode.STATUS_OK) {
					return true;
				}
			} catch (JSONException e) {
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
