package com.croutworst.community.models;

/**
 * Created by adamc on 2/17/17.
 */

public class Event {
	private String title;
	private String description;
	private String location;
	private String endDatetime;
	private String startDatetime;
	private String id;

	public Event() { }

	public Event(String title, String description, String location)
	{
		this.title = title;
		this.description = description;
		this.location = location;
	}

	@Override
	public String toString() {
		return "Event{" +
				"title='" + title + '\'' +
				", description='" + description + '\'' +
				", location'" + location + '\'' +
				'}';
	}

	public String getTitle() { return title; }
	public String getDescription() { return description; }
	public String getLocation() { return location; }
	public String getEndDatetime() { return endDatetime; }
	public String getStartDatetime() { return startDatetime; }
	public String getId() { return id; }

}
