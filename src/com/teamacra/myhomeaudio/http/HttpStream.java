package com.teamacra.myhomeaudio.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.teamacra.myhomeaudio.StreamInterface;

public class HttpStream implements StreamInterface {
	
	private String host = "http://192.168.10.101:8080";
	
	public String[] getMediaList() {
		String[] result = new String[0];
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(this.host+"/song/list");
		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			
			if (entity != null)
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				StringBuilder stringBuilder = new StringBuilder();
				
				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				try {
					JSONArray array = new JSONArray(stringBuilder.toString());
					result = new String[array.length()];
					for (int i=0;i<array.length();i++) {
						result[i] = array.getString(i);
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
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

	@Override
	public boolean play(String name) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(this.host+"/song/play");
		try {
			//List<NameValuePair> postVars = new ArrayList<NameValuePair>(1);
			//postVars.add(new BasicNameValuePair("media", name));
			
			httpPost.setEntity(new StringEntity("{\"song\":\""+name+"\"}\r\n\r\n"));
			
			HttpResponse response = httpClient.execute(httpPost);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean pause() {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(this.host+"/song/pause");
		try {
			
			HttpResponse response = httpClient.execute(httpGet);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
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

}
