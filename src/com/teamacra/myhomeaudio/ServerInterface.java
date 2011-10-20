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
	
	//SYSTEM MODIFICATION
	
	/**
	 * Gets the current user preferences.
	 * @return The user's Preferences, or null if the user is not logged in.
	 */
	public Prefs getPrefs();
	
	/**
	 * Updates the user's preferences.
	 * @param userPrefs A set of Preferences to apply to the current user.
	 * @return True if the preferences are updated successfully.
	 */
	public boolean setPrefs(Prefs userPrefs);
	
	/**
	 * Adds a user to the system.
	 * @param u The user to add.
	 * @return True on success.
	 */
	public boolean addUser(User u);
	
	//AUDIO CONTROLS
	
	/**
	 * Plays the default stream.
	 * @return True on success.
	 */
	public boolean play();
	
	/**
	 * Plays a specified stream.
	 * @param s The Stream to play.
	 * @return True on success.
	 */
	public boolean play(Stream s);
	
	/**
	 * Pauses the currently playing stream.
	 * @return True on success.
	 */
	public boolean pause();
	
	/**
	 * Skips to the next entry in the current stream.
	 * e.g. Skip to the next song in a playlist.
	 * @return True on success.
	 */
	public boolean next();
	
	/**
	 * Skips to the previous entry in the current stream.
	 * e.g. Skip to the previous song in a playlist.
	 * @return
	 */
	public boolean prev();
}
