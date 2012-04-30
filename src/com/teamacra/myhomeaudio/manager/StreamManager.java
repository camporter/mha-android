package com.teamacra.myhomeaudio.manager;

import java.util.ArrayList;
import java.util.Iterator;

import com.teamacra.myhomeaudio.MHAApplication;
import com.teamacra.myhomeaudio.http.HttpSource;
import com.teamacra.myhomeaudio.http.HttpStream;
import com.teamacra.myhomeaudio.node.Node;
import com.teamacra.myhomeaudio.source.Source;
import com.teamacra.myhomeaudio.stream.Stream;

public class StreamManager {
	private ArrayList<Stream> streamList;
	private ArrayList<Source> sourceList;
	private HttpStream httpStream;
	private HttpSource httpSource;
	
	private static StreamManager instance;

	private StreamManager(MHAApplication app) {
		httpStream = new HttpStream(app);
		httpSource = new HttpSource(app);
		streamList = new ArrayList<Stream>();
		sourceList = new ArrayList<Source>();
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
			streamList.clear();
			streamList.addAll(newStreamList);
			return true;
		}
		return false;
	}
	
	public synchronized boolean updateSources() {
		ArrayList<Source> newSourceList = httpSource.getSourceList();
		
		if (newSourceList != null) {
			sourceList.clear();
			sourceList.addAll(newSourceList);
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
	
	public ArrayList<Source> getSourceList() {
		return new ArrayList<Source>(sourceList);
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
	
	public Source getSource(int id) {
		for (Iterator<Source> i = sourceList.iterator(); i.hasNext();) {
			Source nextSource = i.next();
			if (nextSource.id() == id) {
				return new Source(nextSource);
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
	
	/**
	 * Assigns new nodes to the stream on the server.
	 * @param streamId
	 * @param nodes
	 * @return
	 */
	public boolean assignNodes(int streamId, ArrayList<Node> nodes) {
		Stream stream = getStream(streamId);
		
		if (stream != null && httpStream.assignNodes(stream, nodes)) {
			return true;
		}
		return false;
	}
}


