package com.teamacra.myhomeaudio.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.content.SharedPreferences;


public class HttpNodeClient {
	private String host;
	
	public HttpNodeClient(SharedPreferences prefs) {
		this.host = prefs.getString("host", "");
	}
	
	public void sendRSSIValues(ArrayList<String> deviceList) {
		HttpClient httpClient = new DefaultHttpClient();
		try {
			String url = this.host+"/client/rssi";
			System.out.println(url);
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
			
			// TODO: pull nodes to serialized json and send here
			httpPost.setEntity(new StringEntity(jsonOutput));
			
			HttpResponse response = httpClient.execute(httpPost);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
