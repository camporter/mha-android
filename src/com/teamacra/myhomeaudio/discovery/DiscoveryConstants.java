package com.teamacra.myhomeaudio.discovery;

public class DiscoveryConstants {

	public static final int RESPONDER_BROADCAST_PORT = 6808;
	public static final int SEARCH_BROADCAST_PORT = 6809;
	public static final int DATAGRAM_LENGTH = 1024;

	public static final int RESPONDER_SOCKET_TIMEOUT = 250;
	public static final int SEARCH_SOCKET_TIMEOUT = 250;
	public static final int SEARCH_QUERY_INTERVAL = 500;
	
	public static final String SEARCH_HEADER = "SERVICE QUERY ";
	public static final String REPLY_HEADER = "SERVICE REPLY ";
}
