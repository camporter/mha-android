package com.teamacra.myhomeaudio.http;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.media.MediaDescriptor;
import com.teamacra.myhomeaudio.node.Node;
import com.teamacra.myhomeaudio.stream.Stream;
import com.teamacra.myhomeaudio.stream.StreamAction;

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

	/**
	 * Send a request to the server for the list of streams.
	 * 
	 * @return Returns a list of Stream objects.
	 */
	public ArrayList<Stream> getStreamList() {
		ArrayList<Stream> result = new ArrayList<Stream>();

		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", app.getSessionId());
			JSONObject responseObject = executePostRequest("/stream/list", requestObject);

			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK
					&& responseObject.has("streams")) {
				JSONArray streamArray = responseObject.getJSONArray("streams");
				for (int i = 0; i < streamArray.length(); i++) {
					JSONObject next = streamArray.getJSONObject(i);
					result.add(new Stream(next.getInt("id"), next.getString("name")));
				}
				return result;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
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

	public boolean play(Stream stream, int descriptorId, int sourceId) {
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", app.getSessionId());
			requestObject.put("media", descriptorId);
			requestObject.put("source", sourceId);
			requestObject.put("stream", stream.id());
			JSONObject responseObject = executePostRequest("/stream/play", requestObject);
			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK) {
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean pause(Stream stream) {
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", app.getSessionId());
			requestObject.put("stream", stream.id());
			requestObject.put("action", StreamAction.PAUSE);
			JSONObject responseObject = executePostRequest("/stream/action", requestObject);
			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK) {
				return true;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean assignNodes(Stream stream, ArrayList<Node> nodes) {
		JSONObject requestObject = new JSONObject();
		
		JSONArray nodeArray = new JSONArray();
		
		for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
			nodeArray.put(i.next().id());
		}
		
		try {
			requestObject.put("session", app.getSessionId());
			requestObject.put("stream", stream.id());
			requestObject.put("assignedNodes", nodeArray);
			
			JSONObject responseObject = executePostRequest("/stream/assign", requestObject);
			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK) {
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
}
