package com.android.community.models;

import android.view.View;
import android.widget.Toast;

/**
 * Created by adamc on 2/16/17.
 */

public class Meetup{
	private String name;
	private String description;
	private String location;
	private boolean privateMeetup;
	private int attendeeCount;
	private int maxAttendees;
	private int duration;
	private boolean active;

	public String getName() { return this.name; }
	public String getDescription() { return this.description; }
	public String getLocation() { return this.location; }
	public boolean isPrivate(){ return this.privateMeetup; }
	public int getAttendeeCount() { return this.attendeeCount; }
	public int getMaxAttendees() { return this.maxAttendees; }
	public int getDuration() { return this.duration; }
	public boolean isActive() { return this.active; }
}
