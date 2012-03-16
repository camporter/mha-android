package com.teamacra.myhomeaudio.discovery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import android.net.DhcpInfo;

public final class DiscoverySearch {

	protected InetAddress broadcastAddress;
	protected int broadcastPort;

	protected String serviceName;
	protected boolean shouldRun = true;
	protected DatagramSocket socket;
	protected DatagramPacket queuedPacket;
	protected DatagramPacket receivedPacket;

	protected DhcpInfo dhcp;

	public DiscoverySearch(String serviceName, DhcpInfo dhcp) {

		this.dhcp = dhcp;
		this.serviceName = serviceName;

		broadcastPort = DiscoveryConstants.SEARCH_BROADCAST_PORT;
		try {
			broadcastAddress = getBroadcastAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		if (broadcastAddress == null)
			System.exit(1);

		try {
			this.socket = new DatagramSocket(broadcastPort);
			this.socket.setBroadcast(true);
			this.socket.setSoTimeout(DiscoveryConstants.SEARCH_SOCKET_TIMEOUT);

		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public String getServiceName() {
		return serviceName;
	}

	protected String getEncodedServiceName() {
		try {
			return URLEncoder.encode(getServiceName(), "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			return null;
		}
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Starts the actual discovery process.
	 * 
	 * @return The ip address we get back for the discovered server.
	 */
	public String run() {
		int i = 0;
		while (i < 3) {
			DatagramPacket packet = getQueryPacket();
			if (packet != null) {
				queuedPacket = packet;
			}
			sendQueuedPacket();

			try {
				// Wait 800 msecs, latency should be much lower than this on
				// nearly
				// all networks.
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
			int j = 0;
			while (j < 2) {
				try {
					byte[] buf = new byte[DiscoveryConstants.DATAGRAM_LENGTH];
					receivedPacket = new DatagramPacket(buf, buf.length);
					socket.receive(receivedPacket); // This will timeout
													// thankfully
					if (isReplyPacket()) {
						DiscoveryDescription descriptor = null;
						descriptor = getReplyDescriptor();
						if (descriptor != null) {
							// notifyReply(descriptor);
							return receivedPacket.getAddress().getHostAddress();
							// return descriptor;
						}
					}
				} catch (SocketTimeoutException ste) {
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				j++;
			}
			i++;
		}

		return null;

	}

	/**
	 * Closes the socket and does other cleanup.
	 */
	public void end() {
		socket.close();
	}

	protected void sendQueuedPacket() {
		if (queuedPacket == null) {
			return;
		}
		try {
			socket.send(queuedPacket);
			queuedPacket = null;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Figures out if the packet we get back is valid as a reply.
	 * 
	 * @return Whether or not it is a valid reply.
	 */
	protected boolean isReplyPacket() {
		if (receivedPacket == null) {
			return false;
		}

		String dataStr = new String(receivedPacket.getData());
		int pos = dataStr.indexOf((char) 0);
		if (pos > -1) {
			dataStr = dataStr.substring(0, pos);
		}

		if (dataStr.startsWith(DiscoveryConstants.REPLY_HEADER
				+ getEncodedServiceName())) {
			return true;
		}

		return false;
	}

	/**
	 * Builds a DiscoveryDescription from the packet that we receive.
	 * 
	 * @return The generated DiscoveryDescriptor if the packet is formatted
	 *         correctly, or null otherwise.
	 */
	protected DiscoveryDescription getReplyDescriptor() {
		String dataStr = new String(receivedPacket.getData());
		int pos = dataStr.indexOf((char) 0);
		if (pos > -1) {
			dataStr = dataStr.substring(0, pos);
		}

		StringTokenizer tokens = new StringTokenizer(
				dataStr.substring(15 + getEncodedServiceName().length()));
		if (tokens.countTokens() == 4) {
			return DiscoveryDescription.parse(tokens.nextToken(), tokens.nextToken(),
					tokens.nextToken(), tokens.nextToken());
		} else {
			return null;
		}
	}

	/**
	 * Builds a DatagramPacket with the information we need to find a server.
	 * 
	 * @return DatagramPacket assigned with the correct address, port, and data.
	 */
	protected DatagramPacket getQueryPacket() {
		StringBuffer buf = new StringBuffer();
		buf.append(DiscoveryConstants.SEARCH_HEADER + getEncodedServiceName());

		byte[] bytes = buf.toString().getBytes();
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
		packet.setAddress(broadcastAddress);
		packet.setPort(DiscoveryConstants.RESPONDER_BROADCAST_PORT);

		return packet;
	}

	/**
	 * Figures out the local broadcast address for the network that the device
	 * is connected to, using DHCP information.
	 * 
	 * @return The InetAddress associated to the broadcast ip.
	 * @throws UnknownHostException
	 */
	protected InetAddress getBroadcastAddress() throws UnknownHostException {
		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int i = 0; i < 4; i++)
			quads[i] = (byte) ((broadcast >> i * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}
}
