package com.teamacra.myhomeaudio.locations;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * 
 *
 */
public class NodeSignalBoundary {

	private final int id; // node within room
	private ArrayList<NodeSignalRange> foundNodes;

	public NodeSignalBoundary(int i) {
		this.foundNodes = new ArrayList<NodeSignalRange>();
		this.id = i;
	}

	public NodeSignalBoundary(int id, ArrayList<NodeSignalRange> nsr) {
		this.foundNodes = nsr;
		this.id = id;
	}

	public boolean addNodeRange(NodeSignalRange nodeSignalRange) {
		if (!containsNodeById(nodeSignalRange.getNodeId())) {
			foundNodes.add(nodeSignalRange);
			return true;
		}
		return false;
	}

	public NodeSignalRange getNodeRange(int id){
		Iterator<NodeSignalRange> i = foundNodes.iterator();
		NodeSignalRange signal;
		while(i.hasNext()){
			signal = i.next();
			if(signal.getNodeId() == id){
				return signal;
			}
		}
		return null;
	}

	/**
	 * Determines if node with specific id is contained
	 * within the area
	 * @param id Node id to check
	 * @return True - if found, False - if not
	 */
	private boolean containsNodeById(int id) {
		Iterator iterate = foundNodes.iterator();
		while (iterate.hasNext()) {
			if (((NodeSignalRange) iterate.next()).getNodeId() == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return current NodeSignalBoundary node id
	 * @return int Node id
	 */
	public int getNodeId() {
		return id;
	}

	/**
	 * Return number of nodes within area
	 * @return int Number of found nodes
	 */
	public int size() {
		return foundNodes.size();
	}
	
	//{"id":1,"foundNodes":[{"id":1,"min":1,"max":10},{"id":2,"min":2,"max":14},{"id":3,"min":5,"max":7}]}

	public String toJSONString() {
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();
		try{
			object.put("id", id);
			
			Iterator<NodeSignalRange> range = foundNodes.iterator();
			while(range.hasNext()){
				array.put(range.next());
			}
			object.put("foundNodes", array);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return object.toString();
	}
}
