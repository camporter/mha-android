package com.teamacra.myhomeaudio.http;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.node.Node;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.util.Log;


public class HttpNode extends HttpBase {
	private static final String TAG = "HttpNode";
	
	public HttpNode(MHAApplication app) {
		super(app);
	}
	
	/**
	 * Sends a request for the list of nodes on the server.
	 * 
	 * @return An ArrayList of the Nodes. Returns null if the request failed.
	 */
	public ArrayList<Node> getNodes() {
		JSONObject requestObject = new JSONObject();
		
		try {
			requestObject.put("session", app.getSessionId());
			JSONObject responseObject = executePostRequest("/node/list", requestObject);
			
			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK) {
				JSONArray responseArray = responseObject.getJSONArray("nodes");
				ArrayList<Node> resultArray = new ArrayList<Node>(responseArray.length());
				
				for (int i=0; i < responseArray.length(); i++) {
					JSONObject node = responseArray.getJSONObject(i);
					Log.d(TAG, node.toString());
					Node newNode = new Node(node.getInt("id"), node.getString("name"), node.getString("bluetoothaddress"), node.getBoolean("active"));
					resultArray.add(newNode);
				}
				
				return resultArray;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
