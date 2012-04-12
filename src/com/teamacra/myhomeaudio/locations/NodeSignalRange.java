package com.teamacra.myhomeaudio.locations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teamacra.myhomeaudio.manager.NodeManager;

/**
 * Stores node rssi ranges, the
 * maximum and minimum values
 * obtains for a particular node
 *
 */

public class NodeSignalRange{
	private final int id; //node id
	private final int min;
	private final int max;
	
	public NodeSignalRange(int id, int min, int max){
		this.id = id;
		this.min = min;
		this.max = max;
		JSONArray a = new JSONArray();
		
	}
	
	public boolean checkRange(int value){
		if(value >= min && value <= max){
			return true;
		}
		return false;
	}
	
	public int getNodeId(){
		return id;
	}

	public String toJSONString() {
		JSONObject object = new JSONObject();
		try {
			object.put("max", max);
			object.put("min", min);
			object.put("id", id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return object.toString();
	}
	
	public String toString(){
		return "id: "+ id + " min: " + min + " max: " + max;
	}
}
