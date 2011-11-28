package com.teamacra.myhomeaudio.bluetooth;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.teamacra.myhomeaudio.MyHomeAudioActivity;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class DiscoveryService extends Service {
	private final String TAG = "DiscoveryService";
	
	private BluetoothAdapter mAdapter;
	
	private Set<BluetoothDevice> deviceList;
	private Timer timer;
	private int mState;
	
	
	public DiscoveryService() {
		super();
		Log.e(TAG, "wow");
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
		
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Log.i(TAG, "MHA DiscoveryService being started");
		
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
			mAdapter.startDiscovery();
			
			deviceList = mAdapter.getBondedDevices();
		}
	};
	
	
}



