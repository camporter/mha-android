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
	private int min;
	private int max;
	
	public NodeSignalRange(int id){
		this(id, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	public NodeSignalRange(int id, int min, int max){
		this.id = id;
		this.min = min;
		this.max = max;		
	}
	
	public boolean checkRange(int value){
		if(value >= min && value <= max){
			return true;
		}
		return false;
	}
	
	public void setMin(int min){
		this.min = min;
	}
	public void setMax(int max){
		this.max = max;
	}
	
	public int getMin(){
		return min;
	}
	public int getMax(){
		return max;
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
