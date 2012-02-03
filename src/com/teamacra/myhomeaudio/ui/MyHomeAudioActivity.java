package com.teamacra.myhomeaudio.ui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.R;
import com.teamacra.myhomeaudio.R.layout;
import com.teamacra.myhomeaudio.bluetooth.DiscoveryService;
import com.teamacra.myhomeaudio.http.HttpNodeClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

public class MyHomeAudioActivity extends TabActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		addTabs();
	}
	
	public void onStart() {
		super.onStart();
	}
	
	/*
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Discovery has found a device
				Log.i(TAG, "Device was found!");
				
				// Get the corresponding BluetoothDevice object
				final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				
				// get the RSSI value for this action
				Integer rssi = (int) intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
				
				deviceList.add(device.getName());
				deviceList.add(String.valueOf(rssi));
				
				Log.i(TAG, device.getName());
				Log.i(TAG, String.valueOf(rssi));
				
				
				//Intent bIntent = new Intent();
				
				// TODO: Fix this to send RSSIs as well, some other data struct needed
				//bIntent.setAction(DiscoveryService.DEVICE_UPDATE);
				//bIntent.putParcelableArrayListExtra("devices", deviceList);
				//context.sendBroadcast(bIntent);
				
				
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				// Done trying to discover bluetooth devices
				Log.i(TAG, "Discovery finished!");
				
				HttpNodeClient httpNC = new HttpNodeClient((MHAApplication) MyHomeAudioActivity.this.getApplication());
				httpNC.sendRSSIValues(deviceList);
				
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				Log.i(TAG, "Discovery STARTING!");
			}
			
		}
	};
	*/
	
	/**
	 * Adds tabs to the main activity.
	 */
	private void addTabs() {
		Resources resources = getResources();
		TabHost tabHost = getTabHost();

		// The "my" tab
		Intent myIntent = new Intent().setClass(this, MyActivity.class);
		TabHost.TabSpec myTabSpec;
		myTabSpec = tabHost.newTabSpec("my").setIndicator("My").setContent(myIntent);
		tabHost.addTab(myTabSpec);

		// The "users" tab
		Intent usersIntent = new Intent().setClass(this, UsersActivity.class);
		TabHost.TabSpec usersTabSpec;
		usersTabSpec = tabHost.newTabSpec("users").setIndicator("Users").setContent(usersIntent);
		tabHost.addTab(usersTabSpec);

		// The "rooms" tab
		Intent roomsIntent = new Intent().setClass(this, RoomsActivity.class);
		TabHost.TabSpec roomsTabSpec;
		roomsTabSpec = tabHost.newTabSpec("rooms").setIndicator("Rooms").setContent(roomsIntent);
		tabHost.addTab(roomsTabSpec);

		// The "settings" tab
		Intent preferencesIntent = new Intent().setClass(this, PreferencesActivity.class);
		TabHost.TabSpec preferencesTabSpec;
		preferencesTabSpec = tabHost.newTabSpec("Preferences").setIndicator("Preferences")
				.setContent(preferencesIntent);
		tabHost.addTab(preferencesTabSpec);

	}
}
