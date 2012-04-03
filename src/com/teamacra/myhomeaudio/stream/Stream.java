package com.teamacra.myhomeaudio.stream;

import java.util.ArrayList;
import java.util.Map;

import com.teamacra.myhomeaudio.Node;

public class Stream {
	private int id;
	private String name;
	private ArrayList<Node> assignedNodes;
	
	public Stream(int id, String name) {
		this.id = id;
		this.name = name;
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
}
