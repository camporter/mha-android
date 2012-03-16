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
import com.teamacra.myhomeaudio.Node;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;


public class HttpNodeClient extends HttpBase {
	
	public HttpNodeClient(MHAApplication app) {
		super(app);
	}
	
	/**
	 * Sends a request for the list of nodes on the server.
	 * 
	 * @param sessionId Session ID assigned to the client.
	 * @return An ArrayList of the Nodes. Returns null if the request failed.
	 */
	public ArrayList<Node> getNodes(String sessionId) {
		JSONObject requestObject = new JSONObject();
		
		try {
			requestObject.put("session", sessionId);
			JSONObject responseObject = executePostRequest("/node/list", requestObject);
			
			if (responseObject != null && responseObject.getInt("status") == StatusCode.STATUS_OK) {
				JSONArray responseArray = responseObject.getJSONArray("nodes");
				ArrayList<Node> resultArray = new ArrayList<Node>(responseArray.length());
				
				for (int i=0; i < responseArray.length(); i++) {
					JSONObject node = responseArray.getJSONObject(i);
					Node newNode = new Node(node.getInt("id"), node.getString("name"));
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
			String url = this.host+"/client/rssi";
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
}
