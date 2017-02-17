package com.android.community.models;

/**
 * Created by adamc on 2/16/17.
 */

public class Meetup {
	public String name = null;
	public String description = null;
	public Boolean active;

	public String getName() { return this.name; }
	public String getDescription() { return this.description; }
	public Boolean getActive() { return this.active; }
}
