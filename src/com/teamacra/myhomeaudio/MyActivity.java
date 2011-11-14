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

import com.teamacra.myhomeaudio.http.HttpStream;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

public class MyActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my);
		
		ListView mediaListView = (ListView) findViewById(R.id.mediaListView);
		
		String[] mediaArray = new HttpStream().getMediaList();
		
		if(mediaArray != null){
			mediaListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mediaArray));
			mediaListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					new HttpStream().play(((TextView)view).getText().toString());
				}
			});
		}
	}
	
}
