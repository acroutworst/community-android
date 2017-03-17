package com.croutworst.community.deserializer;


import com.croutworst.community.models.AccountRegistration;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import timber.log.Timber;

/**
 * Created by Billy on 2/14/2017.
 */

public class AccountDeserializer implements JsonDeserializer<AccountRegistration>{
    private static final String TAG = "AccountDeserializer";

    @Override
    public AccountRegistration deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        // Get the "account" element from the parsed JSON
        JsonElement account = je.getAsJsonObject().get("data").getAsJsonObject().get("registerAccount").getAsJsonObject().get("account");
        Timber.d("account: " + account);

        // Deserialize it. You use a new instance of Gson to avoid infinite recursion
        // to this deserializer
        return new Gson().fromJson(account, AccountRegistration.class);
    }
}
