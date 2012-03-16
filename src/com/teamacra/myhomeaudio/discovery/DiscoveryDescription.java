package com.teamacra.myhomeaudio.discovery;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class DiscoveryDescription implements Comparable<DiscoveryDescription> {

	private String instanceName;
	private String address;
	private int clientPort;
	private int nodePort;

	public DiscoveryDescription(String instanceName, String address, int clientPort, int nodePort) {
		this.instanceName = instanceName;
		this.address = address;
		this.clientPort = clientPort;
		this.nodePort = nodePort;
	}

	public DiscoveryDescription() {
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String serviceDescription) {
		this.instanceName = serviceDescription;
	}

	/**
	 * Makes the string URL encoded, which makes it easier to deal with in
	 * transit.
	 * 
	 * @return
	 */
	protected String getEncodedInstanceName() {
		try {
			return URLEncoder.encode(getInstanceName(), "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			return null;
		}
	}
	
	public String getAddress() {
		return address;
	}
	
	/**
	 * Gets the port that clients should make requests to.
	 * 
	 * @return
	 */
	public int getClientPort() {
		return clientPort;
	}

	/**
	 * Gets the port that nodes should make requests to.
	 * 
	 * @return
	 */
	public int getNodePort() {
		return nodePort;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}

	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}

	public void setNodePort(int nodePort) {
		this.nodePort = nodePort;
	}

	/**
	 * Get the string representation of the description. When we want to
	 * transmit this data to another client or node, we send it in the format
	 * defined by this method.
	 * 
	 * @return Concatenated string with all of the description's properties.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getEncodedInstanceName());
		buf.append(" ");
		buf.append(address);
		buf.append(" ");
		buf.append(Integer.toString(clientPort));
		buf.append(" ");
		buf.append(Integer.toString(nodePort));
		return buf.toString();
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof DiscoveryDescription)) {
			return false;
		}
		DiscoveryDescription descriptor = (DiscoveryDescription) o;
		return descriptor.getInstanceName().equals(getInstanceName());
	}

	public int hashCode() {
		return getInstanceName().hashCode();
	}

	public int compareTo(DiscoveryDescription sd) throws ClassCastException {
		if (sd == null) {
			throw new NullPointerException();
		}
		if (sd == this) {
			return 0;
		}

		return getInstanceName().compareTo(sd.getInstanceName());
	}

	/**
	 * When we get a packet that should contain the string-based properties
	 * included in a DiscoveryDescription object, we make sure that it follows
	 * the same format used in the toString method.
	 * 
	 * @param encodedInstanceName
	 *            Instance name for the server that is URL encoded.
	 * @param addressAsString
	 *            The IP address of the server.
	 * @param clientPortAsString
	 *            The client port for the server.
	 * @param nodePortAsString
	 *            The node port for the server.
	 * @return A new DiscoveryDescription object that represents the data from
	 *         the given strings.
	 * @see DiscoveryDescription#toString()
	 */
	public static DiscoveryDescription parse(String encodedInstanceName,
			String addressAsString, String clientPortAsString, String nodePortAsString) {

		DiscoveryDescription descriptor = new DiscoveryDescription();
		
		// Put the instance name
		try {
			String name = URLDecoder.decode(encodedInstanceName, "UTF-8");
			if (name == null || name.length() == 0) {
				return null;
			}
			descriptor.setInstanceName(name);
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
			return null;
		}
		
		// Put the address
		descriptor.setAddress(addressAsString);
		
		// Put the client and node ports
		try {
			int p = Integer.parseInt(clientPortAsString);
			descriptor.setClientPort(p);
			p = Integer.parseInt(nodePortAsString);
			descriptor.setNodePort(p);
		} catch (NumberFormatException nfe) {
			System.err.println("Unexpected exception: " + nfe);
			nfe.printStackTrace();
			return null;
		}

		return descriptor;
	}
}
