package com.teamacra.myhomeaudio;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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

	private static final String TAG = "MyHomeAudio";

	public static final String PREFS_NAME = "MyHomeAudioPrefs";

	// Constants defining the messages sent from the DiscoveryService handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_DEVICE_NAME = 4;

	// Intent request constants
	private static final int REQUEST_ENABLE_BT = 3;

	private BluetoothAdapter mBluetoothAdapter = null;
	
	private ArrayList<String> deviceList;
	
	private Timer timer;
	private int mState;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e(TAG, "+++ ON CREATE +++");
		
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		
		// Set the server that we will use
		SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("host", "http://192.168.68.160:8080");
		editor.putString("localIP", Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
		editor.commit();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		checkConnectivity();

		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available on this device.", Toast.LENGTH_SHORT)
					.show();
			this.finish();
			return;
		}
		
		// let the server know the client is ready
		new HttpNodeClient(getSharedPreferences(MyHomeAudioActivity.PREFS_NAME, 0)).sendStart();
		
		deviceList = new ArrayList<String>();
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
		
		addTabs();
	}

	public void onStart() {
		super.onStart();
		Log.e(TAG, "++ ON START ++");

		if (checkBluetooth()) {
			//this.startService(new Intent(this, DiscoveryService.class));
			if (timer != null) {
				
			}
			else {
				timer = new Timer("DiscoveryServiceTimer");
				timer.schedule(updateTask, 0, 30*1000L);
			}
		}
	}
	
	private TimerTask updateTask = new TimerTask() {
		@Override
		public void run() {
			Log.i(TAG, "Trying to run discovery...");
			
			if (!mBluetoothAdapter.isDiscovering()) {
				Log.i(TAG, "Discovery isn't already running...");
				mBluetoothAdapter.startDiscovery();
				Log.i(TAG, "Discovering: "+mBluetoothAdapter.isDiscovering());
			}
		}
	};

	/**
	 * Checks if Bluetooth is on. Prompts user to turn it on if it is off.
	 * 
	 * @return Whether bluetooth is on or not.
	 */
	private boolean checkBluetooth() {
		// Check if bluetooth is on, if not, turn it on
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
			return false;
		}
		return true;
	}

	/**
	 * Checks if the phone's wifi is connected. We want the user to be on wifi,
	 * not a mobile network, so that the phone can communicate with the server.
	 */
	private boolean wifiConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (connectivityManager != null) {
			networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			System.out.println(networkInfo.isConnected());
		}
		return networkInfo == null ? false : networkInfo.isConnected();
	}

	/**
	 * Displays a dialog to close the program if the user isn't on wifi. Doesn't
	 * warn if the user is connected.
	 */
	private void checkConnectivity() {
		if (!wifiConnected()) {
			// Build a dialog box to inform user that wifi is not connected
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("You must be connected to WiFi before you can use My Home Audio");
			builder.setCancelable(false);
			builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {
					// End the app
					MyHomeAudioActivity.this.finish();
				}
			});
			// Show the dialog box
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	/**
	 * Gets activity results and then handles them based off of their request
	 * and result codes.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth was enabled, great
				//this.startService(new Intent(this, DiscoveryService.class));

			} else {
				// Didn't enable bluetooth! End the program
				Toast.makeText(this, "Bluetooth must be available for My Home Audio to run.",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

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
				
				HttpNodeClient httpNC = new HttpNodeClient(getSharedPreferences(MyHomeAudioActivity.PREFS_NAME, 0));
				httpNC.sendRSSIValues(deviceList);
				
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				Log.i(TAG, "Discovery STARTING!");
			}
			
		}
	};
	
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
