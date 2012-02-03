package com.teamacra.myhomeaudio.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.StreamInterface;

public class HttpStream extends HttpBase implements StreamInterface {

	public HttpStream(MHAApplication app) {
		super(app);
	}

	/**
	 * Sends a request to the server for a list of media that the user can play.
	 * 
	 * @return An array of the song names.
	 */
	public String[] getMediaList(String sessionId) {
		String[] result = new String[0];

		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", sessionId);
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

	@Override
	public String getStreamName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArtist() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean play(String sessionId, String songName) {
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", sessionId);
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

	public boolean pause(String sessionId) {
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", sessionId);
			JSONObject responseObject = executePostRequest("/song/pause", requestObject);
			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK) {
				return true;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean next() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean prev() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean play() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pause() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean play(String name) {
		// TODO Auto-generated method stub
		return false;
	}

}
