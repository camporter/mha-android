package com.teamacra.myhomeaudio.bluetooth;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.http.HttpNode;
import com.teamacra.myhomeaudio.http.HttpStream;
import com.teamacra.myhomeaudio.ui.MyHomeAudioActivity;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BluetoothService extends Service {
	private final String TAG = "BluetoothService";
	
	public static final String DEVICE_UPDATE = "com.teamacra.myhomeaudio.bluetooth";
	
	private BluetoothAdapter mAdapter;
	
	private ArrayList<String> deviceList;
	
	private boolean isRunning;
	
	public synchronized ArrayList<String> getDeviceList() {
		return new ArrayList<String>(deviceList);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "MHA BluetoothService being created!");
		
		deviceList = new ArrayList<String>();
		
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
		
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Log.i(TAG, "MHA BluetoothService being started");
		if (!mAdapter.isDiscovering()) {
			isRunning = true;
			mAdapter.startDiscovery();
			while (isRunning) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) { }
			}
			mAdapter.cancelDiscovery();
			stopSelf();
		} else {
			stopSelf();
		}
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "MHA BluetoothService being destroyed!");
		unregisterReceiver(mReceiver);
		super.onDestroy();
		
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
				//bIntent.setAction(BluetoothService.DEVICE_UPDATE);
				//bIntent.putParcelableArrayListExtra("devices", deviceList);
				//context.sendBroadcast(bIntent);
				
				
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				// Done trying to discover bluetooth devices
				Log.i(TAG, "Discovery finished!");
				
				BluetoothService.this.isRunning = false;
				
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				Log.i(TAG, "Discovery STARTING!");
			}
			
		}
	};
	
	
}



