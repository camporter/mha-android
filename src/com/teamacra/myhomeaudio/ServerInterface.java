/**
 * Filename: ServerInterface.java
 * 
 * Description: API for an MHA client to communicate with an MHA server.
 */

package com.teamacra.myhomeaudio;

public interface ServerInterface {
	
	/**
	 * Searches the local network for a server and connects if one is found
	 * @return True if successfully connected to an MHA server
	 */
	public boolean findServer();
	
	/**
	 * Authenticates a user with the MHA server
	 * @param username The username on the system
	 * @param deviceID An ID, unique on the system, to the user's device
	 * @param encryptedPassword The user's system password
	 * @return A User object corresponding to the authenticated user, or
	 * 			null if authentication failed.
	 */
	//TODO: password encryption details
	public User authenticateUser(String username, String deviceID, String encryptedPassword);
	
	/**
	 * Takes a Bluetooth ID from a found device, and submits it to the server.
	 * If it is in fact an MHA node, the server will associate the user with
	 * the node.
	 * @param id The Bluetooth ID of the found device
	 * @return A Node object corresponding to the physical node with this
	 * 			id, or null if no such node exists on the system
	 */
	public Node connectToNode(String id);
	
	//TODO: Continue ServerInterface.java
}
