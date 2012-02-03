package com.teamacra.myhomeaudio;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
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
	private String username;
	private String password;
	private String sessionId;
	
	private boolean bluetoothEnabledDevice;
	
	private String serverAddress;
	private int port = 8080;

	
	@Override
	public void onCreate() {
		super.onCreate();
		this.isLoggedIn = false;

		/*SharedPreferences sharedPrefs = this.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
		// TODO: Do discovery instead of hardcoded value!
		prefsEditor.putString("hostAddress", "http://192.168.68.160:8080");
		prefsEditor.commit();
		*/
		
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
	 * @param username Username for the user logged in.
	 * @param password Password for the user logged in.
	 * @param sessionId Session assigned to the client for the user.
	 */
	public void setLoggedIn(String username, String password, String sessionId) {
		this.username = username;
		this.password = password;
		this.sessionId = sessionId;
		this.isLoggedIn = true;
	}
	
	/**
	 * Set the application state as logged out.
	 */
	public void setLoggedOut() {
		this.isLoggedIn = false;
		this.username = null;
		this.password = null;
		this.sessionId = null;
	}
	
	/**
	 * Is a user logged in?
	 * @return Whether a user is logged in.
	 */
	public boolean isLoggedIn() {
		return this.isLoggedIn;
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
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			return bluetoothAdapter.getName();
		}
		return null;
	}
	
	/**
	 * Update the bluetooth capability of the device.
	 */
	private void checkBluetoothCapability() {
		// Set whether the client is capable of using bluetooth
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			this.bluetoothEnabledDevice = false;
		} else {
			this.bluetoothEnabledDevice = true;
		}
	}
	
	/**
	 * Is the client device capable of bluetooth?
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
			networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			System.out.println(networkInfo.isConnected());
		}
		return networkInfo == null ? false : networkInfo.isConnected();
	}

}
