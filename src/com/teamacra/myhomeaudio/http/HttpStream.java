package com.teamacra.myhomeaudio.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teamacra.myhomeaudio.MHAApplication;

public class HttpStream extends HttpBase {
	
	public HttpStream(MHAApplication app) {
		super(app);
	}

	/**
	 * Sends a request to the server for a list of media that the user can play.
	 * 
	 * @return An array of the song names.
	 */
	public String[] getMediaList() {
		String[] result = new String[0];

		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", app.getSessionId());
			JSONObject responseObject = executePostRequest("/song/list", requestObject);

			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK) {
				JSONArray songArray = responseObject.getJSONArray("songs");

				result = new String[songArray.length()];
				for (int i = 0; i < songArray.length(); i++) {
					result[i] = songArray.getString(i);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;

	}
	
	public String[] getStreamList() {
		String[] result = new String[0];
		
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", app.getSessionId());
			JSONObject responseObject = executePostRequest("/stream/list", requestObject);
			
			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK && responseObject.has("streams")) {
				JSONArray streamArray = responseObject.getJSONArray("streams");
				result = new String[streamArray.length()];
				for (int i = 0; i < streamArray.length(); i++) {
					result[i] = streamArray.getString(i);
				}
			}
		} catch(JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean addStream(String streamName) {
		JSONObject requestObject = new JSONObject();
		JSONObject streamObject = new JSONObject();
		try {
			requestObject.put("session", app.getSessionId());
			streamObject.put("name", streamName);
			requestObject.put("stream", streamObject);
			
			JSONObject responseObject = executePostRequest("/stream/add", requestObject);
			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK) {
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean play(String songName) {
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", app.getSessionId());
			requestObject.put("song", songName);
			JSONObject responseObject = executePostRequest("/song/play", requestObject);
			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK) {
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean pause() {
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", app.getSessionId());
			JSONObject responseObject = executePostRequest("/song/pause", requestObject);
			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK) {
				return true;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
}
