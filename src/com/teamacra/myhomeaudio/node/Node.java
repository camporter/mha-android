package com.teamacra.myhomeaudio.node;

public class Node {

	private int id;
	private String name;
	private String bluetoothAddress;
	private boolean isActive;

	public Node(int id, String name, String bluetoothAddress, boolean active) {
		this.id = id;
		this.name = name;
		this.bluetoothAddress = bluetoothAddress;
		this.isActive = active;
	}

	public Node(Node node) {
		id = node.id();
		name = node.name();
		bluetoothAddress = node.bluetoothAddress();
		isActive = node.isActive();
	}

	public boolean isActive() {
		return isActive;
	}

	public String name() {
		return name;
	}

	public int id() {
		return id;
	}

	public String bluetoothAddress() {
		return bluetoothAddress;
	}

	public String toString() {
		return name;
	}
}
