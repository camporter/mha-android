/**
 * Filename: UserInterface.java
 * 
 * Description: Holds information about a particular user on the system.
 */

package com.teamacra.myhomeaudio;

public interface UserInterface {
	/**
	 * Gets this user's username
	 * @return The username for this user
	 */
	public String getUsername();
	
	/**
	 * Tells whether or not this is a superuser
	 * @return True if this is a superuser
	 */
	public boolean isSuperUser();
	
	/**
	 * Gets the current user preferences
	 * @return The user's Preferences, or null if the user is not logged in
	 */
	public PrefsInterface getPrefs();
	
	/**
	 * Updates the user's preferences
	 * @param userPrefs A set of Preferences to apply to the current user
	 * @return True if the preferences are updated successfully.
	 */
	public boolean setPrefs(PrefsInterface userPrefs);
}
