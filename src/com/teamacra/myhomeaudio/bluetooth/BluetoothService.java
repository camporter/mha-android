package com.teamacra.myhomeaudio.bluetooth;

import java.util.ArrayList;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class BluetoothService extends Service {
	private final String TAG = "BluetoothService";
	
	public static final String DEVICE_UPDATE = "com.teamacra.myhomeaudio.bluetooth.device";
	public static final String DISCOVERY_START = "com.teamacra.myhomeaudio.bluetooth.start";
	public static final String DISCOVERY_FINISH = "com.teamacra.myhomeaudio.bluetooth.finish";
	
	private BluetoothAdapter mAdapter;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "BluetoothService is being created");
		
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
		
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Log.i(TAG, "BluetoothService is being started");
		if (!mAdapter.isDiscovering()) {
			mAdapter.startDiscovery();
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
				Log.i(TAG, "A bluetooth device was found");
				
				// Get the corresponding BluetoothDevice object
				final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				
				// get the RSSI value for this device
				Integer rssi = (int) intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
				
				// Broadcast to anyone listening all about the device found
				Intent bIntent = new Intent();
				bIntent.setAction(BluetoothService.DEVICE_UPDATE);
				bIntent.putExtra("deviceName", device.getName());
				bIntent.putExtra("deviceAddress", device.getAddress());
				bIntent.putExtra("deviceRssi", rssi);
				context.sendBroadcast(bIntent);
				
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				// Done trying to discover bluetooth devices
				Log.i(TAG, "Bluetooth discovery finished");
				
				Intent finishIntent = new Intent();
				finishIntent.setAction(BluetoothService.DISCOVERY_FINISH);
				context.sendBroadcast(finishIntent);
				
				BluetoothService.this.mAdapter.cancelDiscovery();
				BluetoothService.this.stopSelf();
				
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				Log.i(TAG, "Bluetooth discovery starting");
				Intent startIntent = new Intent();
				startIntent.setAction(BluetoothService.DISCOVERY_START);
			}
			
		}
	};
	
	
}



