package com.teamacra.myhomeaudio.discovery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.net.wifi.WifiManager.MulticastLock;
import android.util.Log;

public final class DiscoverySearchMulti {

	protected static InetAddress multicastAddress;
	protected static int multicastPort;

	static {
		try {
			multicastAddress = InetAddress
					.getByName(DiscoveryConstants.MULTICAST_ADDRESS);
			multicastPort = DiscoveryConstants.MULTICAST_PORT;
		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
		}
	}

	protected String serviceName;
	protected boolean shouldRun = true;
	protected MulticastSocket socket;
	protected DatagramPacket queuedPacket;
	protected DatagramPacket receivedPacket;
	// protected Vector<DiscoverySearchListener> listeners;
	protected Thread searchThread;
	protected Timer myTimer;

	public DiscoverySearchMulti() {
		try {
			socket = new MulticastSocket(multicastPort);
			socket.joinGroup(multicastAddress);
			socket.setSoTimeout(DiscoveryConstants.SEARCH_SOCKET_TIMEOUT);

		} catch (IOException ioe) {
			System.err.println("Unexpected exception: " + ioe);
			ioe.printStackTrace();
			// System.exit(1);
		}

		// listeners = new Vector<DiscoverySearchListener>();
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

	/*
	 * public void addServiceBrowserListener(DiscoverySearchListener l) { if
	 * (!listeners.contains(l)) { listeners.add(l); } }
	 * 
	 * public void removeServiceBrowserListener(DiscoverySearchListener l) {
	 * listeners.remove(l); }
	 * 
	 * public void startLookup() { if (myTimer == null) { myTimer = new
	 * Timer("QueryTimer"); myTimer.scheduleAtFixedRate(new QueryTimerTask(),
	 * 0L, DiscoveryConstants.SEARCH_QUERY_INTERVAL); } }
	 * 
	 * public void startSingleLookup() { if (myTimer == null) { myTimer = new
	 * Timer("QueryTimer"); myTimer.schedule(new QueryTimerTask(), 0L); myTimer
	 * = null; } }
	 * 
	 * public void stopLookup() { if (myTimer != null) { myTimer.cancel();
	 * myTimer = null; } }
	 * 
	 * protected void notifyReply(DiscoveryDescription descriptor) { for
	 * (DiscoverySearchListener l : listeners) { l.serviceReply(descriptor); } }
	 * 
	 * public void startListener() { if (searchThread == null) { shouldRun =
	 * true; searchThread = new Thread(this, "DiscoverySearch");
	 * searchThread.start(); } }
	 * 
	 * public void stopListener() { if (searchThread != null) { shouldRun =
	 * false; searchThread.interrupt(); searchThread = null; } }
	 */

	public String run() {
		DatagramPacket packet = getQueryPacket();
		if (packet != null) {
			queuedPacket = packet;
		}
		sendQueuedPacket();
		
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		int count = 0;
		while (count < 2)
		{
			try {
				byte[] buf = new byte[DiscoveryConstants.DATAGRAM_LENGTH];
				receivedPacket = new DatagramPacket(buf, buf.length);
				socket.receive(receivedPacket); // note timeout in effect
				if (isReplyPacket()) {
					DiscoveryDescription descriptor = null;
					descriptor = getReplyDescriptor();
					if (descriptor != null) {
						//notifyReply(descriptor);
						return descriptor.getAddress().getHostAddress();
					}
				}
			} catch (SocketTimeoutException ste) {
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			count++;
		}
		
		return null;
		
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

	protected DiscoveryDescription getReplyDescriptor() {
		String dataStr = new String(receivedPacket.getData());
		int pos = dataStr.indexOf((char) 0);
		if (pos > -1) {
			dataStr = dataStr.substring(0, pos);
		}

		StringTokenizer tokens = new StringTokenizer(
				dataStr.substring(15 + getEncodedServiceName().length()));
		if (tokens.countTokens() == 4) {
			return DiscoveryDescription.parse(tokens.nextToken(),
					tokens.nextToken(), tokens.nextToken(), tokens.nextToken());
		} else {
			return null;
		}
	}

	protected DatagramPacket getQueryPacket() {
		StringBuffer buf = new StringBuffer();
		buf.append(DiscoveryConstants.SEARCH_HEADER + getEncodedServiceName());

		byte[] bytes = buf.toString().getBytes();
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
		packet.setAddress(multicastAddress);
		packet.setPort(multicastPort);

		return packet;
	}

	/*
	 * private class QueryTimerTask extends TimerTask {
	 * 
	 * public void run() { DatagramPacket packet = getQueryPacket(); if (packet
	 * != null) { queuedPacket = packet; } } }
	 */
}
