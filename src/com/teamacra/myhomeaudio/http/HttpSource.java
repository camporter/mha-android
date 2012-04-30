package com.teamacra.myhomeaudio.http;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.media.MediaDescriptor;
import com.teamacra.myhomeaudio.source.Source;

public class HttpSource extends HttpBase {

	public HttpSource(MHAApplication app) {
		super(app);
	}

	public ArrayList<Source> getSourceList() {
		ArrayList<Source> result = new ArrayList<Source>();

		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", app.getSessionId());

			JSONObject responseObject = executePostRequest("/source/list",
					requestObject);

			if (responseObject != null
					&& responseObject.getInt("status") == StatusCode.STATUS_OK
					&& responseObject.has("sources")) {
				JSONArray sourceArray = responseObject.getJSONArray("sources");
				for (int i = 0; i < sourceArray.length(); i++) {
					JSONObject next = sourceArray.getJSONObject(i);
					result.add(new Source(next.getInt("id"), next
							.getString("name")));
				}
				return result;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<MediaDescriptor> getSourceMedia(int sourceId) {
		ArrayList<MediaDescriptor> result = new ArrayList<MediaDescriptor>();

		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("session", app.getSessionId());
			requestObject.put("source", sourceId);

			JSONObject responseObject = executePostRequest("/source/media",
					requestObject);

			if (responseObject != null
					&& responseObject.getInt("status") == StatusCode.STATUS_OK
					&& responseObject.has("media")) {
				JSONArray mediaArray = responseObject.getJSONArray("media");
				for (int i = 0; i < mediaArray.length(); i++) {
					JSONObject next = mediaArray.getJSONObject(i);
					result.add(new MediaDescriptor(next.getInt("id"), next
							.getString("title"), next.getString("artist"), next
							.getString("album"), next.getString("genre")));
				}
				return result;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}
}
