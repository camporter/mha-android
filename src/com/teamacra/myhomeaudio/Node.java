package com.teamacra.myhomeaudio;


public class Node {
	private int id;
	private String name;
	
	public Node(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String name() {
		return name;
	}
	
	public int id() {
		return id;
	}
}
