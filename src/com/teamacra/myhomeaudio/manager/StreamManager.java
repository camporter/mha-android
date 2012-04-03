package com.teamacra.myhomeaudio.manager;

import java.util.ArrayList;
import java.util.Iterator;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.http.HttpStream;
import com.teamacra.myhomeaudio.stream.Stream;

public class StreamManager {
	private ArrayList<Stream> streamList;
	private HttpStream httpStream;
	
	private static StreamManager instance;

	private StreamManager(MHAApplication app) {
		httpStream = new HttpStream(app);
	}
	
	public synchronized static StreamManager getInstance(MHAApplication app) {
		if (instance == null) {
			instance = new StreamManager(app);
		}
		
		return instance;
	}
	
	/**
	 * Updates the list of stream objects.
	 * 
	 * @return Whether the stream list update succeeded.
	 */
	public synchronized boolean updateStreams() {
		
		ArrayList<Stream> newStreamList = httpStream.getStreamList();
		
		if (newStreamList != null) {
			streamList = newStreamList;
			return true;
		}
		return false;
	}

	/**
	 * Gets a list of Stream objects.
	 * <p>
	 * Note, this doesn't automatically update from the server. Make sure to
	 * call updateStreams() at your discretion.
	 * 
	 * @return
	 */
	public ArrayList<Stream> getStreamList() {
		return new ArrayList<Stream>(streamList);
	}

	/**
	 * Get a Stream by its name.
	 * 
	 * @param name
	 *            The name of the Stream.
	 * @return The Stream object, or null if a matching stream object wasn't
	 *         found.
	 */
	public Stream getStream(String name) {
		for (Iterator<Stream> i = streamList.iterator(); i.hasNext();) {
			Stream nextStream = i.next();
			if (nextStream.name().equals(name)) {
				return new Stream(nextStream);
			}
		}
		return null;
	}

	/**
	 * Get a Stream by its id.
	 * 
	 * @param id
	 *            The id of the Stream.
	 * @return The Stream object, or null if a matching stream object wasn't
	 *         found.
	 */
	public Stream getStream(int id) {
		for (Iterator<Stream> i = streamList.iterator(); i.hasNext();) {
			Stream nextStream = i.next();
			if (nextStream.id() == id) {
				return new Stream(nextStream);
			}
		}
		return null;
	}
	
	public boolean addStream(String name) {
		if (httpStream.addStream(name) && updateStreams()) {
			return true;
		}
		return false;
	}
}
