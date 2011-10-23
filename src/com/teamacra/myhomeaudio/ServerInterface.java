/**
 * Filename: ServerInterface.java
 * 
 * Description: API for an MHA client to communicate with an MHA server.
 */

package com.teamacra.myhomeaudio;

public interface ServerInterface {

	//INITIALIZATION
	/**
	 * Searches the local network for a server and connects if one is found
	 * @return True if successfully connected to an MHA server
	 */
	public boolean connect();
	
	/**
	 * Connects to a specified server
	 * @param ipAddress The IP address of the server
	 * @return True on success
	 */
	public boolean connect(String ipAddress);
	
	/**
	 * Disconnects from the server
	 * @return True on success
	 */
	public boolean disconnect();
	
	/**
	 * Authenticates a user with the MHA server
	 * @param username The username on the system
	 * @param deviceID An ID, unique on the system, to the user's device
	 * @param hashedPassword The user's system password
	 * @return A User object corresponding to the authenticated user, or
	 * 			null if authentication failed.
	 */
	public UserInterface login(String username, String deviceID,
			String hashedPassword);
	
	/**
	 * Takes a Bluetooth ID from a found device, and submits it to the server.
	 * If it is in fact an MHA node, the server will associate the user with
	 * the node.
	 * @param id The Bluetooth ID of the found device
	 * @return A Node object corresponding to the physical node with this
	 * 			id, or null if no such node exists on the system
	 */
	public NodeInterface connectToNode(String id);
	
	//SYSTEM MODIFICATION
	/**
	 * Adds a user to the system.
	 * @param u The user to add.
	 * @return True on success.
	 */
	public boolean addUser(UserInterface u);

	/**
	 * Removes a user from the system
	 * @param u The UserInterface to remove
	 * @return True on success
	 */
	public boolean removeUser(UserInterface u);

	/**
	 * Updates a user on the system
	 * @param u The UserInterface to update
	 * @return True on success
	 */
	public boolean updateUser(UserInterface u);

	/**
	 * Returns a list of all users on the system
	 * @return A UserInterface array of all the users
	 */
	public UserInterface[] listUsers();
	
	/**
	 * Returns all streams available to the user.
	 * @return An array of Streams.
	 */
	public StreamInterface[] getAvailableStreams();
}
