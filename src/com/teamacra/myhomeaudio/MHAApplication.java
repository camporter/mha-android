package com.teamacra.myhomeaudio;

import android.app.Application;
import android.util.Log;


public class MHAApplication extends Application {
	public static final String TAG = "MyHomeAudio";
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Application created");
	}
	
	@Override
	public void onTerminate() {
		Log.d(TAG, "Application terminated");
		super.onTerminate();
	}
	
	
}
