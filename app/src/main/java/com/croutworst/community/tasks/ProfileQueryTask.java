package com.croutworst.community.tasks;

import android.os.AsyncTask;

import com.croutworst.community.authentication.Communicator;

import timber.log.Timber;

/**
 * Created by adamc on 2/15/17.
 */

public class ProfileQueryTask extends AsyncTask<String, Void, Boolean> {
	private static String TAG = "ProfileQueryTask";
	private Communicator communicator;
	public String email;

	@Override
	protected Boolean doInBackground(String... params) { // params[0] = username; params[1] = password
		boolean successful;
		// Retrofit HTTP call to login

		// for debug worker thread
		if(android.os.Debug.isDebuggerConnected())
			android.os.Debug.waitForDebugger();

		try {
			communicator = new Communicator();
			communicator.queryProfilePost();

			successful = communicator.successful;

			Timber.d("Query isSuccessful: " + communicator.successful);
			Timber.d("QUERYTASK_SUCCESSFUL: " + successful);
		} catch (Exception e) {
			e.printStackTrace();
			Timber.d("THE QUERY WAS A FAILURE");

			return false;
		}

		Timber.d(TAG, "successful2: " + successful);
		return successful;
	}

	@Override
	protected void onPostExecute(final Boolean successful) {
		Timber.d("inside onPostExecute");

		if (successful) {
			Timber.d("inside onPostExecute isSuccessful: " + successful);

		} else {
			Timber.d("inside onPostExecute isFailure: " + successful);

		}

	}
}
