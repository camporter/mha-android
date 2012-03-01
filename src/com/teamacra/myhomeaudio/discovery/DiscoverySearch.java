package com.teamacra.myhomeaudio.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.net.DhcpInfo;
import android.util.Log;

public class DiscoverySearch {
	public static final int PORT = 7890;
	public static final int DISCOVERY_PORT = 8901;
	private InetAddress broadcastAddress;
	
	public DiscoverySearch(DhcpInfo dhcp) throws UnknownHostException {
		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int i=0; i < 4; i++)
			quads[i] = (byte) ((broadcast >> i * 8) & 0xFF);
		this.broadcastAddress = InetAddress.getByAddress(quads);
	}
	
	public String run() {
		String data ="MHA CLIENT";
		DatagramSocket socket;
		try {
			socket = new DatagramSocket(PORT);
			socket.setBroadcast(true);
			DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), broadcastAddress, DISCOVERY_PORT);
			int j = 0;
			while (j < 6) {
				Log.e("myhomeaudio", "RUNNING!");
				socket.send(packet);
				j++;
			}
			socket.close();
		} catch (Exception e) {
			Log.e("myhomeaudio", "Something crapped out");
			return null;
		}
		return "boop";
		
	}
	
	
}
