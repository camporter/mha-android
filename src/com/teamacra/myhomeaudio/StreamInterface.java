/**
 * Filename: StreamInterface.java
 * 
 * Description: Holds information about a stream available on the server.
 */

package com.teamacra.myhomeaudio;

public interface StreamInterface {
	
	//STATE INFORMATION
	/**
	 * Gets the stream name
	 * @return The stream name
	 */
	public String getStreamName();
	
	/**
	 * Gets the title of the currently playing song
	 * TODO: Android Activity objects already have a getTitle method!
	 * @return The title of the song
	 */
	//public String getTitle();
	
	/**
	 * Gets the artist of the currently playing song
	 * @return The artist of the song
	 */
	public String getArtist();
	
	//AUDIO CONTROLS
	
	/**
	 * Plays the default stream.
	 * @return True on success.
	 */
	public boolean play();
	
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
