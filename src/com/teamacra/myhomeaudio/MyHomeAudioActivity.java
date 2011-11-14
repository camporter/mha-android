package com.teamacra.myhomeaudio;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TabHost;

public class MyHomeAudioActivity extends TabActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		checkConnectivity();
		checkBluetooth();
		addTabs();
	}

	/**
	 * Checks if Bluetooth is on. Prompts user to turn it on if it is off.
	 */
	private void checkBluetooth() {
		//Check if bluetooth is on, if not, turn it on
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if(adapter != null){
			if (!adapter.isEnabled()) {
				Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBTIntent, 0);
			}
		}
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
			networkInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			System.out.println(networkInfo.isConnected());
		}
		return networkInfo == null ? false : networkInfo.isConnected();
	}
	
	/**
	 * Displays a dialog to close the program if the user isn't on wifi. Doesn't warn if the user is connected.
	 */
	private void checkConnectivity() {
		if (!wifiConnected())
		{
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
			/*builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// try again
					if (wifiConnected())
					{
						dialog.cancel();
					}
				}
			});*/
			
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
	
	/**
	 * Add tabs to the main activity.
	 */
	private void addTabs() {
		Resources resources = getResources();
		TabHost tabHost = getTabHost();

		// The "my" tab
		Intent myIntent = new Intent().setClass(this, MyActivity.class);
		TabHost.TabSpec myTabSpec;
		myTabSpec = tabHost.newTabSpec("my").setIndicator("My")
				.setContent(myIntent);
		tabHost.addTab(myTabSpec);

		// The "users" tab
		Intent usersIntent = new Intent().setClass(this, UsersActivity.class);
		TabHost.TabSpec usersTabSpec;
		usersTabSpec = tabHost.newTabSpec("users").setIndicator("Users")
				.setContent(usersIntent);
		tabHost.addTab(usersTabSpec);

		// The "rooms" tab
		Intent roomsIntent = new Intent().setClass(this, RoomsActivity.class);
		TabHost.TabSpec roomsTabSpec;
		roomsTabSpec = tabHost.newTabSpec("rooms").setIndicator("Rooms")
				.setContent(roomsIntent);
		tabHost.addTab(roomsTabSpec);

		// The "settings" tab
		Intent preferencesIntent = new Intent().setClass(this,
				PreferencesActivity.class);
		TabHost.TabSpec preferencesTabSpec;
		preferencesTabSpec = tabHost.newTabSpec("Preferences")
				.setIndicator("Preferences").setContent(preferencesIntent);
		tabHost.addTab(preferencesTabSpec);

	}
}