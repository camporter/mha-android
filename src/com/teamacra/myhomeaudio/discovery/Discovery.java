package com.teamacra.myhomeaudio.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Random;

import javax.jmdns.JmDNS;
import javax.jmdns.JmmDNS;
import javax.jmdns.ServiceInfo;

import android.net.wifi.WifiManager.MulticastLock;
import android.util.Log;


public class Discovery {
	public static final String DISCOVERY_TYPE = "_myhomeaudio._tcp.local.";
	
	public Discovery() {
		
	}
	
	public String run(MulticastLock lock) {
		lock.acquire();
		try {
			// Discovery must be set on all possible network interfaces, so go
			// through them all
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets)) {
				// Each interface can have multiple addresses
				Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					// Ignore loopback or link-local
					//if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
						JmDNS jmdns = JmDNS.create(inetAddress, null);
						
						ServiceInfo[] serviceInfos = jmdns.list(DISCOVERY_TYPE);
						
						jmdns.close();
					//}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		lock.release();
		return null;
	}
}
