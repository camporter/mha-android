package com.teamacra.myhomeaudio.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.SharedPreferences;


public class HttpNode {
	private String host;
	
	public HttpNode(SharedPreferences prefs) {
		this.host = prefs.getString("host", "");
	}
	
	public void sendRSSIValues() {
		HttpClient httpClient = new DefaultHttpClient();
		try {
			String url = this.host+"/node/rssi";
			System.out.println(url);
			HttpPost httpPost = new HttpPost(url);
			
			// TODO: pull nodes to serialized json and send here
			httpPost.setEntity(new StringEntity(""));
			
			HttpResponse response = httpClient.execute(httpPost);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
