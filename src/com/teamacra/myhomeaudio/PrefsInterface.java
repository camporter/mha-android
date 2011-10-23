/**
 * Filename: PrefsInterface.java
 * 
 * Description: Holds user preferences. Used for easy fetch/push of preferences
 * from/to the server.
 */

package com.teamacra.myhomeaudio;

public interface PrefsInterface {
	/**
	 * Gets the default stream
	 * @return The default StreamInterface
	 */
	public StreamInterface getDefaultStream();
	
	/**
	 * Sets the default stream
	 * @param s A StreamInterface on the system
	 * @return True on success
	 */
	public boolean setDefaultStream(StreamInterface s);
	
	/**
	 * Gets the stream the user has playing
	 * @return The active StreamInterface
	 */
	public StreamInterface getActiveStream();
	
	/**
	 * Sets the desired stream to play
	 * @param s The StreamInterface to play
	 * @return True if successfully set
	 */
	public boolean setActiveStream(StreamInterface s);
	
	/**
	 * Gets the time frames for this user.
	 * @return An array of TimeFrames
	 */
//	public TimeFrame[] getTimeFrames();
	
	/**
	 * Removes a time frame from the system
	 * @param tfID The ID of the TimeFrame to remove
	 * @return True on success
	 */
//	public boolean removeTimeFrame(int tfID);
	
	/**
	 * Adds a time frame to the system
	 * @param t The TimeFrame to add
	 * @return True on success, false if there's an overlap
	 */
//	public boolean addTimeFrame(TimeFrame t);
}
