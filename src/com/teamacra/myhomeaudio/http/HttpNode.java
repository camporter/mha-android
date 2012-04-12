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


public class HttpNode extends HttpBase {
	
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
					Node newNode = new Node(node.getInt("id"), node.getString("name"), "");
					resultArray.add(newNode);
				}
				
				return resultArray;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Sends a request for the list of active nodes on the server.
	 * 
	 * @return An ArrayList of the active nodes. Returns null if the request failed.
	 */
	public ArrayList<Node> getActiveNodes() {
		JSONObject requestObject = new JSONObject();
		
		try {
			requestObject.put("session", app.getSessionId());
			JSONObject responseObject = executePostRequest("/node/activelist", requestObject);
			
			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK) {
				JSONArray responseArray = responseObject.getJSONArray("nodes");
				ArrayList<Node> resultArray = new ArrayList<Node>(responseArray.length());
				
				for (int i=0; i < responseArray.length(); i++) {
					JSONObject node = responseArray.getJSONObject(i);
					Node newNode = new Node(node.getInt("id"), node.getString("name"), "");
					resultArray.add(newNode);
				}
				
				return resultArray;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void sendRSSIValues(ArrayList<String> deviceList) {
		
		try {
			String url = "http://"+app.getServerAddress()+":"+app.getPort()+"/client/rssi";
			System.out.println("Sending RSSI values to server");
			HttpPost httpPost = new HttpPost(url);
			
			String jsonOutput = "[ ";
			
			int i =0;
			
			for (String item : deviceList) {
				if (i == 0)
				{
					jsonOutput += "{ \"name\" : \""+item+"\", ";
					i++;
				} else {
					jsonOutput += "\"rssi\" : "+item+" },";
					i--;
				}
			}
			
			jsonOutput += " ]";
			
			httpPost.setEntity(new StringEntity(jsonOutput));
			
			HttpResponse response = this.httpClient.execute(httpPost);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int updateConfiguration(ArrayList<Node> newConfiguration) {
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("nodes", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return StatusCode.STATUS_FAILED;
	}
}
