package com.teamacra.myhomeaudio.manager;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.locations.NodeSignature;
import com.teamacra.myhomeaudio.node.Node;

public class LocationManager {
	private ArrayList<Device> nodes;
	private static LocationManager instance;
	private MHAApplication app;
	
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
		this.nodes = new ArrayList<Device>();
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
			nodes.add(new Device(node.id(), rssi));
			return true;
		}
		return false;
	}
	
	public void clear(){
		nodes.clear();
	}
	
	public JSONArray getLocationJSONArray(){
		JSONArray array = new JSONArray();
		for(Device device : nodes){
			array.put(new Device(device));
		}
		return array;
	}

}
