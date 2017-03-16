package com.android.community.deserializer;


import com.android.community.models.Account;
import com.android.community.models.AccountRegistration;
import com.android.community.models.Profile;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import timber.log.Timber;

/**
 * Created by adamc on 2/15/17.
 */

public class UserDeserializer implements JsonDeserializer<Account> {

	private static final String TAG = "UserDeserializer";

	@Override
	public Account deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
		// Get the "account" element from the parsed JSON
		JsonElement user = je.getAsJsonObject().get("data").getAsJsonObject().get("myProfile").getAsJsonObject().get("user");
		Timber.d("user: " + user);

		// Deserialize it. You use a new instance of Gson to avoid infinite recursion
		// to this deserializer
		return new Gson().fromJson(user, Account.class);
	}
}
