/**
 * Filename: UserInterface.java
 * 
 * Description: Holds information about a particular user on the system.
 */

package com.teamacra.myhomeaudio;

public interface UserInterface {
	//TODO: Write User.java
	
	/**
	 * Gets this user's username.
	 * @return The username for this user.
	 */
	public String getUsername();
	
	/**
	 * Tells whether or not this is a superuser.
	 * @return True if this is a superuser.
	 */
	public boolean isSuperUser();

}
