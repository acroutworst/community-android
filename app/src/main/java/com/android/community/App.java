package com.android.community;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by adamc on 3/16/17.
 */

public class App extends Application {
	@Override public void onCreate() {
		super.onCreate();

		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		} 
	}
}
