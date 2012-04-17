package com.teamacra.myhomeaudio.locations;

import org.json.JSONException;
import org.json.JSONObject;

import com.teamacra.myhomeaudio.node.Node;

/**
 * For any room, each node has a signal range. The NodeSignalRange object
 * represents what a single node's ranges are within that room. We can get the
 * max and min values that were recorded.
 * 
 */
public class NodeSignalRange {

	private Node node;
	private Integer min;
	private Integer max;

	public NodeSignalRange(Node node) {
		this.node = node;
	}

	public boolean checkRange(int value) {
		if (value >= min && value <= max) {
			return true;
		}
		return false;
	}
	
	/**
	 * Stores the rssi value.
	 * 
	 * @param rssi The RSSI value that our node's signal is at.
	 */
	public void storeRSSIValue(int rssi) {
		if (min == null && max ==null) {
			// No RSSI values have been recorded yet
			min = rssi;
			max = rssi;
		}
		else if (rssi < min) {
			min = rssi;
		} else if (rssi > max) {
			max = rssi;
		}
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public Node getNode() {
		return node;
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		try {
			object.put("max", max);
			object.put("min", min);
			object.put("id", node.id());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return object;
	}

	public String toString() {
		return "id: " + node.id() + " min: " + min + " max: " + max;
	}
}
