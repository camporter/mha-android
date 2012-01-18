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

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;


public class HttpNodeClient extends HttpBase {
	
	public HttpNodeClient(SharedPreferences prefs) {
		super(prefs);
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
	
	/*public void sendStart() {
		HttpClient httpClient = new DefaultHttpClient();
		try {
			String url = this.host+"/client/start";
			System.out.println("Sending start request to server");
			HttpPost httpPost = new HttpPost(url);
			
			
			httpPost.setEntity(new StringEntity(this.localIPAddress));
			HttpResponse response = this.httpClient.execute(httpPost);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
