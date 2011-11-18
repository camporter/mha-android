package com.teamacra.myhomeaudio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MyActivity extends Activity implements StreamInterface {

	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my);
		
		ListView mediaListView = (ListView) findViewById(R.id.mediaListView);
		String[] mediaArray = getMediaList();
		
		mediaListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mediaArray));
	}
	
	public String[] getMediaList() {
		String[] result = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://192.168.10.101:8080/song/list");
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
	public boolean next() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean prev() {
		// TODO Auto-generated method stub
		return false;
	}
}
