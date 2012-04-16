package com.teamacra.myhomeaudio;

import java.util.Calendar;

import com.teamacra.myhomeaudio.bluetooth.BluetoothService;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

/**
 * An Application class extension that stores all the global information for our
 * application.
 * 
 * @author Cameron
 * 
 */
public class MHAApplication extends Application {

	public static final String TAG = "MyHomeAudio";
	public static final String PREFS_NAME = "MyHomeAudioPrefs";

	private boolean isLoggedIn;
	private boolean isConfigured;
	private String username;
	private String password;
	private String sessionId;

	private boolean bluetoothEnabledDevice;

	private PendingIntent discoveryPendingIntent;

	private String serverAddress;
	private int port = 8080;

	@Override
	public void onCreate() {
		super.onCreate();
		this.isLoggedIn = false;
		this.isConfigured = false;

		checkBluetoothCapability();

		Log.d(TAG, "Application created");
	}

	@Override
	public void onTerminate() {
		setLoggedOut();
		Log.d(TAG, "Application terminated");
		super.onTerminate();
	}

	/**
	 * Set the application state as logged in.
	 * 
	 * @param username
	 *            Username for the user logged in.
	 * @param password
	 *            Password for the user logged in.
	 * @param sessionId
	 *            Session assigned to the client for the user.
	 * @param configured
	 *            Configuration status for the user
	 */
	public void setLoggedIn(String username, String password, String sessionId,
			boolean configured) {
		this.username = username;
		this.password = password;
		this.sessionId = sessionId;
		this.isLoggedIn = true;
		this.isConfigured = configured;
	}

	/**
	 * Set the application state as logged out.
	 */
	public void setLoggedOut() {
		this.isLoggedIn = false;
		this.isConfigured = false;
		this.username = null;
		this.password = null;
		this.sessionId = null;
	}

	/**
	 * Is a user logged in?
	 * 
	 * @return Whether a user is logged in.
	 */
	public boolean isLoggedIn() {
		return this.isLoggedIn;
	}

	public boolean isConfigured() {
		return this.isConfigured;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getServerAddress() {
		return this.serverAddress;
	}

	public int getPort() {
		return this.port;
	}

	public String getLocalAddress() {
		if (isWifiConnected()) {
			WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int addr = wifiInfo.getIpAddress();
			return Formatter.formatIpAddress(addr);
		} else {
			return null;
		}
	}

	public String getMacAddress() {
		if (isWifiConnected()) {
			WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			return wifiInfo.getMacAddress();
		} else {
			return null;
		}
	}

	/**
	 * Use this to start the service to find other bluetooth signals near the
	 * device.
	 * 
	 * @param c
	 *            The context to create the intent in.
	 * @param repeating
	 *            Whether to schedule the service to continuously run.
	 */
	public void startBluetoothService(Context c, boolean repeating) {
		Intent serviceIntent = new Intent(c, BluetoothService.class);
		
		if (repeating) {
			final AlarmManager alarmManager = (AlarmManager) this
					.getSystemService(ALARM_SERVICE);
			discoveryPendingIntent = PendingIntent.getService(c, 0, serviceIntent, 0);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), 14000, discoveryPendingIntent);
		} else {
			c.startService(serviceIntent);
		}
	}

	/**
	 * 
	 */
	public void stopBluetoothService() {
		if (discoveryPendingIntent != null) {
			final AlarmManager alarmManager = (AlarmManager) this
					.getSystemService(ALARM_SERVICE);
			alarmManager.cancel(discoveryPendingIntent);
		}
		
		Intent serviceIntent = new Intent(this, BluetoothService.class);
		this.stopService(serviceIntent);
	}

	public String getSessionId() {
		return this.sessionId;
	}

	/**
	 * Gets the name of the device's bluetooth.
	 * 
	 * @return The name if the device is bluetooth capable. Otherwise null.
	 */
	public String getBluetoothName() {
		checkBluetoothCapability();

		if (this.bluetoothEnabledDevice) {
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter
					.getDefaultAdapter();
			return bluetoothAdapter.getName();
		}
		return null;
	}

	/**
	 * Update the bluetooth capability of the device.
	 */
	private void checkBluetoothCapability() {
		// Set whether the client is capable of using bluetooth
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			this.bluetoothEnabledDevice = false;
		} else {
			this.bluetoothEnabledDevice = true;
		}
	}

	/**
	 * Is the client device capable of bluetooth?
	 * 
	 * @return
	 */
	public boolean isBluetoothCapableDevice() {
		return this.bluetoothEnabledDevice;
	}

	/**
	 * Checks if the device WiFi is connected. We want the client to be on WiFi,
	 * not a mobile network, so that the device can communicate with the server.
	 */
	public boolean isWifiConnected() {
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
}
