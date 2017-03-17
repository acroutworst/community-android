package com.croutworst.community.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.croutworst.community.AccountService;
import com.croutworst.community.DataAdapter;
import com.android.community.R;
import com.croutworst.community.authentication.Communicator;
import com.croutworst.community.authentication.ServerRequestInterface;
import com.croutworst.community.models.Event;
import com.google.gson.Gson;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class EventFragment extends Fragment {

    private static String TAG = "EventFragment";

    /* Initialize Views */
    private ViewGroup view;
    private RecyclerView recyclerView;
    private ArrayList<Event> data;
    private DataAdapter adapter;

		private SwipeRefreshLayout mSwipeRefreshLayout;
		private FloatingActionButton fab;

		private View scrollView;
		private View mProgressView;
		private AsyncTask mAuthTask = null;

		private Boolean successful = false;

		private Dialog dialog;
		private EditText eventName;
		private EditText eventDescription;
		private EditText eventLocation;
		private MaterialBetterSpinner spinner;
		private CheckBox mPrivateCheckbox;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (ViewGroup) inflater.inflate(R.layout.fragment_events, container, false);
        initViews();
				setupClickListeners();

        return view;
    }

    private void initViews() {
				mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
				mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
						android.R.color.holo_green_light,
						android.R.color.holo_orange_light,
						android.R.color.holo_red_light);
				recyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_view_events);
        recyclerView.setHasFixedSize(true);
				adapter = new DataAdapter();
				recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

				fab = (FloatingActionButton) view.findViewById(R.id.fab_events);

				queryEventPost();
    }

		private void setupClickListeners() {
				mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						queryEventPost();
						mSwipeRefreshLayout.setRefreshing(false);
					}
				});

			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showMyDialog(getContext());
				}
			});
		}

    private void queryEventPost() {
        successful = false;

        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        String API_TOKEN = "Bearer " + AccountService.Instance().mAuthToken;

			Timber.d("EventFragment AuthToken: " + API_TOKEN);

        Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://community-ci.herokuapp.com")
            .build();
        ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);
        Call<ResponseBody> call = service.apiEventPost(API_TOKEN, makeEventQuery());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

							try {
								final String body = response.body().string();

								adapter.removeEvent();

								if(!body.isEmpty()) {
									getActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											Gson gson = new Gson();
											try {
												JSONArray jsonEvents = new JSONObject(body).getJSONObject("data").getJSONObject("allEvents").getJSONArray("edges");
												for (int i = 0; i < jsonEvents.length(); ++i) {
													JSONObject event = jsonEvents.getJSONObject(i).getJSONObject("node");
													adapter.addEvent(gson.fromJson(event.toString(), Event.class));
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									});
								} else {
									Timber.d("Response Body is null");
									Timber.d("Response Body: " + body);
								}

							} catch(Exception e) {
								Timber.d(e.getMessage());
								Timber.d("Events Body ERROR");
							}
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
							Timber.d(t.getMessage());
							Timber.d("onFailure in enqueue");
            }
        });
    }

    private String makeEventQuery() {
        return "{allEvents {\nedges{\nnode { title, description, location }}}}";
    }

	private void showMyDialog(Context context) {
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.event_custom_dialog);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);

		TextView textView = (TextView) dialog.findViewById(R.id.add_event_dialog_title);
		Button btnBtmLeft = (Button) dialog.findViewById(R.id.add_event_btn);
		eventName = (EditText) dialog.findViewById(R.id.eventname);
		eventDescription = (EditText) dialog.findViewById(R.id.eventdescription);
		eventLocation = (EditText) dialog.findViewById(R.id.eventlocation);
		spinner = (MaterialBetterSpinner) dialog.findViewById(R.id.event_spinner);

		mPrivateCheckbox = (CheckBox) dialog.findViewById(R.id.event_private_checkbox);
		if(mPrivateCheckbox.isChecked()){
			mPrivateCheckbox.setChecked(true);
		} else{
			mPrivateCheckbox.setChecked(false);
		}

		String[] items = new String[]{"Q29tbXVuaXR5Tm9kZTox", "Q29tbXVuaXR5Tm9kZToy", "Q29tbXVuaXR5Tm9kZTo1"};
		ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, items);
		spinner.setAdapter(adapter);

		btnBtmLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Timber.d("Event Name: " + eventName.getText().toString());
				Timber.d("Event Description: " + eventDescription.getText().toString());
				Timber.d("Event Location: " + eventLocation.getText().toString());

//				mAuthTask = new AddEventTask(eventName.getText().toString(), eventDescription.getText().toString(), eventLocation.getText().toString());
				createEventPost(eventName.getText().toString(), eventDescription.getText().toString(), eventLocation.getText().toString());
				dialog.dismiss();
			}
		});

		/**
		 * if you want the dialog to be specific size, do the following
		 * this will cover 85% of the screen (85% width and 85% height)
		 */
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int dialogWidth = (int)(displayMetrics.widthPixels * 0.85);
		int dialogHeight = (int)(displayMetrics.heightPixels * 0.85);
		dialog.getWindow().setLayout(dialogWidth, dialogHeight);

		dialog.show();
	}

	/**
	 * Represents an asynchronous registration task used to authenticate
	 * the user.
	 */
	private class AddEventTask extends AsyncTask<Void, Void, Boolean> {
		private Communicator communicator;

		private final String mTitle;
		private final String mDescription;
		private final String mLocation;


		AddEventTask(String mTitle, String mDescription, String mLocation) {
			this.mTitle = mTitle;
			this.mDescription = mDescription;
			this.mLocation = mLocation;
		}

		@Override
		protected Boolean doInBackground(Void... params) { // params[0] = username; params[1] = password
			boolean successful;

			// for debug worker thread
			if(android.os.Debug.isDebuggerConnected())
				android.os.Debug.waitForDebugger();

			try {
				// Retrofit HTTP call to login
				communicator = new Communicator();
				communicator.addEventPost(mTitle, mDescription, mLocation);

				successful = communicator.successful;

				Timber.d("ADD_EVENT_TASK_SUCCESSFUL: " + successful);
			} catch (Exception e) {
				Timber.d("THE QUERY WAS A FAILURE");

				e.printStackTrace();

				return false;
			}
			return successful;
		}

		@Override
		protected void onPostExecute(final Boolean successful) {
			mAuthTask = null;
			showProgress(false);
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime).alpha(
					show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}


	private void createEventPost(String title, String description, String location){
		//Here a logging interceptor is created
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);

		//The logging interceptor will be added to the http client
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		httpClient.addInterceptor(logging);

		String API_TOKEN = "Bearer " + AccountService.Instance().mAuthToken;

		Timber.d("EventFragment AuthToken: " + API_TOKEN);

		Retrofit retrofit = new Retrofit.Builder()
				.client(httpClient.build())
				.addConverterFactory(GsonConverterFactory.create())
				.baseUrl("https://community-ci.herokuapp.com")
				.build();
		ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);
		Call<ResponseBody> call = service.apiCreateMeetupPost(API_TOKEN, registerEvent(title, description, location));

		call.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

				try {
					final String body = response.body().string();

					adapter.removeEvent();

					if(!body.isEmpty()) {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								try {
									boolean meetupCreated = new JSONObject(body).getJSONObject("data").getJSONObject("registerEvent").getBoolean("ok");
									Timber.d(TAG, "eventCreated : " + meetupCreated);

									queryEventPost();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					} else {
						Timber.d("Response Body is null");
						Timber.d("Response Body: " + body);
					}

				} catch(Exception e) {
					Timber.d(e.getMessage());
					Timber.d("Events Body ERROR");
				}
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t){
				Timber.d(t.getMessage());
				Timber.d("Failed creating the event");
			}
		});
	}

	private String registerEvent(String title, String description, String location) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("PST"));
		Date currentLocalTime = cal.getTime();
		DateFormat date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
		date.setTimeZone(TimeZone.getTimeZone("PST"));
		String localTime = date.format(currentLocalTime);
		return String.format("mutation{\nregisterEvent(community:\"%s\", title:\"%s\", description:\"%s\", location:\"%s\", private:%s, startDatetime:\"%s\", endDatetime:\"%s\"){ok, event{id, title, description, location}}}", "Q29tbXVuaXR5Tm9kZTox", title, description, location, false, "2017-03-10 22:02:09.573784+00", "2017-03-10 22:02:09.573784+00");
	}
}
