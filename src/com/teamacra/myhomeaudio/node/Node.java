package com.teamacra.myhomeaudio.node;


public class Node {
	private int id;
	private String name;
	private String bluetoothaddress;
	private boolean isActive;
	
	public Node(int id, String name, String bluetoothaddress, boolean active) {
		this.id = id;
		this.name = name;
		this.bluetoothaddress = bluetoothaddress;
		this.isActive = active;
	}
	
	public Node(Node node) {
		id = node.id();
		name = node.name();
		bluetoothaddress = node.bluetoothaddress();
		isActive = node.isActive();
	}

	public boolean isActive(){
		return isActive;
	}
	public String name() {
		return name;
	}
	
	public int id() {
		return id;
	}
	
	public String bluetoothaddress() {
		return bluetoothaddress;
	}
	
	public String toString() {
		return id+" "+name;
	}
}
