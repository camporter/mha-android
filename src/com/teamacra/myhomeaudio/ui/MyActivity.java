package com.teamacra.myhomeaudio.ui;

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

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.R;
import com.teamacra.myhomeaudio.R.id;
import com.teamacra.myhomeaudio.R.layout;
import com.teamacra.myhomeaudio.http.HttpStream;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

public class MyActivity extends Activity {

	String[] mediaArray;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my);

		ListView mediaListView = (ListView) findViewById(R.id.mediaListView);
		String sessionId = ((MHAApplication) this.getApplication()).getSessionId();
		this.mediaArray = new HttpStream((MHAApplication) this.getApplication())
				.getMediaList(sessionId);

		if (mediaArray != null) {
			mediaListView.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, mediaArray));
			mediaListView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String sessionId = ((MHAApplication) MyActivity.this.getApplication())
							.getSessionId();
					String songName = ((TextView) view).getText().toString();
					new HttpStream((MHAApplication) MyActivity.this.getApplication()).play(
							sessionId, songName);
				}
			});
		}
	}

}
