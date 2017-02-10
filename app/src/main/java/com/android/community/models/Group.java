package com.android.community.models;

import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by adamc on 2/10/17.
 */

public class Group {
	private String title = null;
	private String description = null;
	// Change this to support Picasso when we query for group pics
	private int image = -1;

	public String getTitle() { return this.title; }
	public String getDescription() { return this.description; }
	public int getImage() { return this.image; }
}
