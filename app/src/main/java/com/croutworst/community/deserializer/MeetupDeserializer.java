package com.croutworst.community.deserializer;

import com.croutworst.community.models.Meetup;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import timber.log.Timber;

/**
 * Created by adamc on 2/16/17.
 */

public class MeetupDeserializer implements JsonDeserializer<Meetup> {
	private static final String TAG = "AccountDeserializer";

	@Override
	public Meetup deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
		// Get the "account" element from the parsed JSON
		JsonElement meetup = je.getAsJsonObject().get("data").getAsJsonObject().get("allMeetups").getAsJsonObject().get("edges")
				.getAsJsonObject().get("node");
		Timber.d("meetup: " + meetup);

		// Deserialize it. You use a new instance of Gson to avoid infinite recursion
		// to this deserializer
		return new Gson().fromJson(meetup, Meetup.class);
	}
}
