package com.android.community.models;

/**
 * Created by adamc on 3/6/17.
 */

public class Community {
	private String id = null;
	private String title = null;
	private String description = null;
	private String acronym = null;
	private String phoneNumber = null;
	private String slug = null;

	public Community() { }

	public Community(String title, String description, String acronym)
	{
		this.title = title;
		this.description = description;
		this.acronym = acronym;
	}

	@Override
	public String toString() {
		return "Event{" +
				"title='" + title + '\'' +
				", description='" + description + '\'' +
				", location'" + acronym + '\'' +
				'}';
	}

	public String getId() { return id; }
	public String getTitle() { return title; }
	public String getDescription() { return description; }
	public String getAcronym() { return acronym; }
	public String getPhoneNumber() { return phoneNumber; }
	public String getSlug() { return slug; }

}
