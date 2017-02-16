package com.android.community.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.android.community.authentication.Communicator;

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

			Log.d(TAG, "Query isSuccessful: " + communicator.successful);
			Log.d(TAG, "QUERYTASK_SUCCESSFUL: " + successful);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("QUERY_POST_FAILURE", "THE QUERY WAS A FAILURE");

			return false;
		}

		Log.d(TAG, "successful2: " + successful);
		return successful;
	}

	@Override
	protected void onPostExecute(final Boolean successful) {
		Log.d(TAG, "inside onPostExecute");

		if (successful) {
			Log.d(TAG, "inside onPostExecute isSuccessful: " + successful);

		} else {
			Log.d(TAG, "inside onPostExecute isFailure: " + successful);

		}

	}
}
