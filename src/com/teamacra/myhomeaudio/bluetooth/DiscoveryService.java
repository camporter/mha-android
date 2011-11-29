package com.teamacra.myhomeaudio.bluetooth;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.teamacra.myhomeaudio.MyHomeAudioActivity;

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

public class DiscoveryService extends Service {
	private final String TAG = "DiscoveryService";
	
	private BluetoothAdapter mAdapter;
	
	private Set<BluetoothDevice> deviceList;
	private Timer timer;
	private int mState;
	
	boolean discoveryRunning = false;
	
	
	public DiscoveryService() {
		super();
	}
	
	public int getState() {
		return mState;
	}
	
	public synchronized ArrayList<BluetoothDevice> getDeviceList() {
		return new ArrayList<BluetoothDevice>(deviceList);
	}
	
	private synchronized void setState(int state) {
		mState = state;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "MHA DiscoveryService being created!");
		
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Log.i(TAG, "MHA DiscoveryService being started");
		
		if (timer != null) {
			updateTask.cancel();
			timer.cancel();
			timer.purge();
		}

		timer = new Timer("DiscoveryServiceTimer");
		timer.schedule(updateTask, 0, 30*1000L);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "MHA DiscoveryService being destroyed!");
		timer.cancel();
		timer = null;
	}
	
	private TimerTask updateTask = new TimerTask() {
		@Override
		public void run() {
			Log.i(TAG, "Running discovery...");
			if (!discoveryRunning) {
				Log.i(TAG, "Discovery isn't already running...");
				mAdapter.startDiscovery();
				discoveryRunning = true;
			}
		}
	};
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			// Discovery has found a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				Log.i(TAG, "Device was found!");
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				deviceList.add(device);
				Log.i(TAG, device.getName());
				
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				discoveryRunning = false;
				Log.i(TAG, "Discovery finished!");
			}
			
		}
	};
	
	
}



