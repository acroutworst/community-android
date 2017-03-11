package com.android.community.models;

/**
 * Created by adamc on 2/16/17.
 */

public class Meetup {
	private String name = null;
	private String description = null;
	private String location = null;
	private int maxAttendees;
	private boolean privateMeetup = false;
	private Boolean active;

	public String getName() { return this.name; }
	public String getDescription() { return this.description; }
	public String getLocation() { return this.location; }
	public int getMaxAttendees() { return this.maxAttendees; }
	public boolean isPrivate(){ return this.privateMeetup; }
	public Boolean isActive() { return this.active; }
}
