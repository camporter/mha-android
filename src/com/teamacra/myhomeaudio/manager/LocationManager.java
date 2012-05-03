package com.teamacra.myhomeaudio.manager;

import java.util.ArrayList;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.locations.NodeSignature;
import com.teamacra.myhomeaudio.node.Node;
import android.util.Log;

public class LocationManager {
	private ArrayList<Device> devices;
	private static LocationManager instance;
	private MHAApplication app;
	private String TAG = "LocationManager";
	
	private class Device{
		private final int id;
		private final int rssi;
		
		private Device(int id, int rssi){
			this.id = id;
			this.rssi = rssi;
		}
		
		private Device(Device device){
			this.id = device.id;
			this.rssi = device.rssi;
		}
		
		private JSONObject toJSONObject(){
			JSONObject obj = new JSONObject();
			try {
				obj.put("id", id);
				obj.put("rssi", rssi);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return obj;
		}
	}

	private LocationManager(MHAApplication app) {
		this.app = app;
		this.devices = new ArrayList<Device>();
	}

	public synchronized static LocationManager getInstance(
			MHAApplication app) {
		if (instance == null) {
			instance = new LocationManager(app);
		}
		return instance;
	}
	
	public boolean storeNode(String name, String bluetoothAddress, int rssi){
		NodeManager nm = NodeManager.getInstance(app);
		Node node = nm.getNode(name, bluetoothAddress,  true);
		if(node != null){
			Device device = getDevice(node);
			if(getDevice(node) == null){
				devices.add(new Device(node.id(),rssi));				
			}else{
				devices.set(devices.indexOf(device),new Device(node.id(),rssi));
				Log.d(TAG,"Replacing " + node.name() + " rssi " + device.rssi + " to "+rssi);
			}
		}
	
		return false;
	}
	
	private Device getDevice(Node node) {
		for(Device device : devices){
			if(device.id == node.id()){
				return device;
			}
		}
		return null;
	}

	public void clear(){
		devices.clear();
	}
	
	public JSONArray getLocationJSONArray(){
		JSONArray array = new JSONArray();
		for(Device device : devices){
			array.put(new Device(device).toJSONObject());
		}
		return array;
	}
}
