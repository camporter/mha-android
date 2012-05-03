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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (bluetoothAddress == null) {
			if (other.bluetoothAddress != null)
				return false;
		} else if (!bluetoothAddress.equals(other.bluetoothAddress))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
