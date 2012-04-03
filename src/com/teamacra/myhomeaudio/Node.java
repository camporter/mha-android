package com.teamacra.myhomeaudio;


public class Node {
	private int id;
	private String name;
	private String bluetoothaddress;
	
	public Node(int id, String name, String bluetoothaddress) {
		this.id = id;
		this.name = name;
		this.bluetoothaddress = bluetoothaddress;
	}
	
	public Node(Node node) {
		id = node.id();
		name = node.name();
		bluetoothaddress = node.bluetoothaddress();
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
}
