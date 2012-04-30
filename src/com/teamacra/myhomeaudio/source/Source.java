package com.teamacra.myhomeaudio.source;

import java.util.ArrayList;

import com.teamacra.myhomeaudio.media.MediaDescriptor;


public class Source {
	private int id;
	private String name;
	private ArrayList<MediaDescriptor> mediaList;
	
	public Source(int id, String name) {
		this.id = id;
		this.name = name;
		this.mediaList = new ArrayList<MediaDescriptor>();
	}
	
	public Source(int id, String name, ArrayList<MediaDescriptor> mediaList) {
		this.id = id;
		this.name = name;
		this.mediaList = mediaList;
	}
	
	public Source(Source source) {
		this.id = source.id();
		this.name = source.name();
		this.mediaList = source.mediaList();
	}
	
	public int id() {
		return id;
	}
	
	public String name() {
		return name;
	}
	
	public ArrayList<MediaDescriptor> mediaList() {
		return mediaList;
	}
	
	public void setMediaList(ArrayList<MediaDescriptor> mediaList) {
		this.mediaList = mediaList;
	}
	
	public String toString() {
		return name;
	}
}
