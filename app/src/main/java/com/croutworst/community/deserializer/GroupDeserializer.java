package com.croutworst.community.deserializer;


import com.croutworst.community.models.Group;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import timber.log.Timber;

/**
 * Created by adamc on 3/7/17.
 */

public class GroupDeserializer implements JsonDeserializer<Group> {
	private static final String TAG = "GroupDeserializer";

	@Override
	public Group deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
		// Get the "account" element from the parsed JSON
		JsonElement account = je.getAsJsonObject().get("data").getAsJsonObject().get("registerAccount").getAsJsonObject().get("account");
		Timber.d("group: " + account);

		// Deserialize it. You use a new instance of Gson to avoid infinite recursion
		// to this deserializer
		return new Gson().fromJson(account, Group.class);
	}
}
