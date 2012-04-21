package com.teamacra.myhomeaudio.stream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teamacra.myhomeaudio.node.Node;

public class Stream {
	private int id;
	private String name;
	private ArrayList<Node> assignedNodes;

	public Stream(int id, String name) {
		this.id = id;
		this.name = name;
		this.assignedNodes = new ArrayList<Node>();
	}

	public Stream(Stream stream) {
		this.id = stream.id();
		this.name = stream.name();
		this.assignedNodes = stream.getAssignedNodes();
	}

	public int id() {
		return id;
	}

	public String name() {
		return name;
	}

	public String toString() {
		return name;
	}

	public ArrayList<Node> getAssignedNodes() {
		return new ArrayList<Node>(assignedNodes);
	}

	/**
	 * Sets the nodes that the stream is assigned to. Don't use this outside of
	 * the StreamManager class, it won't do anything otherwise!
	 * 
	 * @param newAssignedNodes
	 * 
	 * @see StreamManager
	 */
	public void setAssignedNodes(ArrayList<Node> newAssignedNodes) {
		this.assignedNodes.clear();
		this.assignedNodes.addAll(newAssignedNodes);
	}

	public JSONObject toJSONObject() {
		JSONObject result = new JSONObject();

		try {
			result.put("id", id);
			result.put("name", name);

			result.put("assignedNodes", new JSONArray(assignedNodes));

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}
}
