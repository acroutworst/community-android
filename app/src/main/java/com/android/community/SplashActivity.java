package com.android.community;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.community.authentication.Communicator;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private AsyncTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            Thread.sleep(2000);

            mAuthTask = new ClientTokenTask().execute();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public class ClientTokenTask extends AsyncTask<String, Void, Boolean> {
        private Communicator communicator;

        @Override
        protected Boolean doInBackground(String... params) { // params[0] = username; params[1] = password
            boolean successful;
            // Retrofit HTTP call to login

            // for debug worker thread
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            try {
                communicator = new Communicator();
                communicator.client = Communicator.ClientType.BASECLIENT;
                communicator.clientLoginPost();

                successful = communicator.successful;
                Log.d(TAG, "USERLOGINTASK_SUCCESSFUL: " + successful);
                Log.d("LOGIN_POST_SUCCESS", "THE LOGIN POST WAS SUCCESSFUL 123");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("LOGIN_POST_FAILURE", "THE LOGIN POST WAS A FAILURE");

                return false;
            }

            Log.d(TAG, "successful2: " + successful);
            return successful;
        }

        @Override
        protected void onPostExecute(final Boolean successful) {
            mAuthTask = null;
            Log.d(TAG, "inside onPostExecute");

            if (successful) {
                Log.d(TAG, "inside if(successful)");
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Log.d(TAG, "Client Token POST not successful");

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

