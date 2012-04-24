package com.teamacra.myhomeaudio.media;

import org.json.JSONException;
import org.json.JSONObject;

public class MediaDescriptor {
	private final int id;
	private final String title;
	private final String artist;
	private final String album;
	private final String genre;

	public MediaDescriptor(int id, String title, String artist, String album,
			String genre) {
		this.id = id;
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.genre = genre;
	}

	public MediaDescriptor(int id) {
		this.id = id;
		this.title = null;
		this.artist = null;
		this.album = null;
		this.genre = null;
	}

	public MediaDescriptor(MediaDescriptor descriptor) {
		this.id = descriptor.id();
		this.title = descriptor.title();
		this.artist = descriptor.artist();
		this.album = descriptor.album();
		this.genre = descriptor.genre();
	}

	public int id() {
		return id;
	}

	public String title() {
		return title;
	}

	public String artist() {
		return artist;
	}

	public String album() {
		return album;
	}

	public String genre() {
		return genre;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return title;
	}
	
	public JSONObject toJSONObject() {
		JSONObject result = new JSONObject();
		
		try {
			result.put("id", id);
			result.put("title", title);
			result.put("artist", artist);
			result.put("album", album);
			result.put("genre", genre);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
