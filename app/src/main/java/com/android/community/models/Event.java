package com.android.community.models;

/**
 * Created by adamc on 2/17/17.
 */

public class Event {
	private String title;
	private String description;
	private String location;


	public Event(String title,String description,String location)
	{
		this.title = title;
		this.description = description;
		this.location = location;
	}


	public String getTitle() { return this.title; }
	public String getDescription() { return this.description; }
	public String getLocation() { return this.location; }
}
