package com.android.community.deserializer;

import android.util.Log;

import com.android.community.models.AccountRegistration;
import com.android.community.models.Profile;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by adamc on 2/15/17.
 */

public class ProfileDeserializer implements JsonDeserializer<Profile> {
	private static final String TAG = "ProfileDeserializer";

	@Override
	public Profile deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
		// Get the "account" element from the parsed JSON
		JsonElement profile = je.getAsJsonObject().get("data").getAsJsonObject().get("myProfile");
		Log.d(TAG, "profile: " + profile);

		// Deserialize it. You use a new instance of Gson to avoid infinite recursion
		// to this deserializer
		return new Gson().fromJson(profile, Profile.class);
	}
}
