package com.android.community.deserializer;

import android.util.Log;

import com.android.community.models.AccountRegistration;
import com.android.community.models.Event;
import com.android.community.models.EventResponse;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;

import okhttp3.ResponseBody;

/**
 * Created by adamc on 2/17/17.
 */

public class EventDeserializer implements JsonDeserializer<Collection<ResponseBody>> {
	private static final String TAG = "EventDeserializer";

	@Override
	public Collection<ResponseBody> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
		// Get the "account" element from the parsed JSON
		JsonElement event = je.getAsJsonObject().get("data").getAsJsonObject().get("allEvents").getAsJsonObject().get("edges");

		Log.d(TAG, "event: " + event);

		Type collectionType = new TypeToken<Collection<EventResponse>>(){}.getType();
		Collection<ResponseBody> enums = new Gson().fromJson(event, collectionType);

		// Deserialize it. You use a new instance of Gson to avoid infinite recursion
		// to this deserializer
//		return new Gson().fromJson(event, EventResponse.class);
		return enums;
	}
}
