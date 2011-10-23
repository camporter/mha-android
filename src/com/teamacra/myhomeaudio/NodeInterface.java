/**
 * Filename: NodeInterface.java
 * 
 * Description: Holds all of the relevant information about an MHA node.
 */

package com.teamacra.myhomeaudio;

public interface NodeInterface {
	
	/**
	 * Gets the name of this node
	 * @return The node's name
	 */
	public String getName();
	
	/**
	 * Gets the current stream playing from the node
	 * @return The StreamInterface being played
	 */
	public StreamInterface getNodeStream();
	
	/**
	 * Gets the list of users connected to this node
	 * @return A UserInterface array of the users
	 */
	public UserInterface[] getUsers();
}
