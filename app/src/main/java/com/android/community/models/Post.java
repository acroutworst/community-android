package com.android.community.models;

/**
 * Created by adamc on 3/14/17.
 */

public class Post {
	private boolean active;
	public String text;
	private String createDate;
	private String id;

	public Post() { }

	public Post(String text)
	{
		this.text = text;
	}

	@Override
	public String toString() {
		return "Event{" +
				"text='" + text + '\'' +
				'}';
	}

	public boolean getActive () { return active; }
	public String getText() { return text; }
	public String getCreateDate() { return createDate; }
	public String getId() { return id; }
}
