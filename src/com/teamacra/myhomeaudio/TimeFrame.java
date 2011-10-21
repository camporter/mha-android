/**
 * Filename: TimeFrame.
 * 
 * Description: Sets a specific stream to be played in a particular room(s), during a
 * particular time period.
 */

package com.teamacra.myhomeaudio;

import java.util.Vector;

public class TimeFrame {
	private int startHour, startMin, endHour, endMin; //Start and end times
	private StreamInterface stream; //The stream to play
	private Vector<NodeInterface> nodes; //The rooms this time frame applies to
	private int id; //An internal id for the system to quickly reference this node
	
	/**
	 * Creates an empty timeframe object
	 */
	public TimeFrame(){
		startHour = startMin = endHour = endMin = 0;
		stream = null;
		nodes = null;
		id = -1;
	}
	
	/**
	 * Creates a new TimeFrame object
	 * @param sH The hour to start the time frame (0-23)
	 * @param sM The minutes to start the time frame (0-59)
	 * @param eH The hour to end the time frame (0-23)
	 * @param eM The hour to end the time frame (0-59)
	 * @param str The stream to play during the time frame
	 * @param ns The set of nodes (i.e., the rooms) to apply this time frame to
	 * @param i An internal id for the system
	 */
	@SuppressWarnings("unchecked")
	public TimeFrame(int sH, int sM, int eH, int eM, StreamInterface str,
			Vector<NodeInterface> ns, int i){
		//make sure the times are proper
		if(sH > eH || (sH == eH && sM > eM) || //starts after it ends
				startHour > 23 || startHour < 0 || //Start hour not [0-23]
				endHour > 23 || endHour < 0 || //End hour not [0-23]
				startMin < 0 || startMin > 59 || //Start min not [0-59]
				endMin < 0 || endMin > 59){ //End min not [0-23]
			//create an empty time frame
			startHour = startMin = endHour = endMin = 0;
			stream = null;
			nodes = null;
			id = -1;
		} else {
			startHour = sH;
			startMin = sM;
			endHour = eH;
			endMin = eM;
			stream = str;
			nodes = (Vector<NodeInterface>) ns.clone();
			id = i;
		}
	}
	
	/**
	 * Gets the hour that this time frame starts
	 * @return The hour (0-23) this time frame starts
	 */
	public int getStartHour(){
		return startHour;
	}
	
	/**
	 * Gets the minute that this time frame starts
	 * @return The minute (0-59) this time frame starts
	 */
	public int getStartMin(){
		return startMin;
	}

	/**
	 * Gets the hour that this time frame ends
	 * @return The hour (0-23) this time frame ends
	 */
	public int getEndHour(){
		return endHour;
	}
	
	/**
	 * Gets the minute that this time frame ends
	 * @return The minute (0-59) this time frame ends
	 */
	public int getEndMin(){
		return endMin;
	}

	/**
	 * Gets the stream for this time frame
	 * @return The StreamInterface for this time frame
	 */
	public StreamInterface getStream(){
		return stream;
	}
	
	/**
	 * Gets the rooms for this time frame
	 * @return The Vector of NodeInterfaces corresponding to this time frame
	 */
	public Vector<NodeInterface> getRooms(){
		return nodes;
	}

	/**
	 * Gets the internal ID for this time frame
	 * @return The internal ID for this time frame
	 */
	public int getID(){
		return id;
	}
}
