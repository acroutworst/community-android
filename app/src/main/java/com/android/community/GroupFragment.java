package com.android.community;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.community.authentication.Communicator;
import com.android.community.authentication.ServerRequestInterface;
import com.android.community.models.Community;
import com.android.community.models.Event;
import com.android.community.models.Group;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class GroupFragment extends Fragment {
    final private String TAG = "GroupFragment";

		/* Initialize Views */
    private RecyclerView recyclerView;
    private ViewGroup view;
    private GridAdapter adapter;
		private SwipeRefreshLayout mSwipeRefreshLayout;
		private FloatingActionButton fab;

		private View scrollView;
		private View mProgressView;
		private AsyncTask mAuthTask = null;

		private Dialog dialog;
		private EditText groupName;
		private EditText groupDescription;
		private Spinner dropdown;
		private ArrayList<String> c_options = new ArrayList<>();
		private HashMap<String, String> comm_options = new HashMap<>();

	@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = (ViewGroup) inflater.inflate(R.layout.fragment_group, container, false);

				initViews();
				setupClickListeners();
				queryGroupPost();

        return view;
    }

		private void initViews() {
			mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.group_swipe_refresh_layout);
			mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
					android.R.color.holo_green_light,
					android.R.color.holo_orange_light,
					android.R.color.holo_red_light);
			recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

			int spanCount = getSpanCount();

			adapter = new GridAdapter();

			// We are using a multi span grid to show many color models in each row. To set this up we need
			// to set our span count on the adapter and then get the span size lookup object from
			// the adapter. This look up object will delegate span size lookups to each model.
			adapter.setSpanCount(spanCount);
			GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), spanCount);
			gridLayoutManager.setSpanSizeLookup(adapter.getSpanSizeLookup());
			gridLayoutManager.setRecycleChildrenOnDetach(true);

			recyclerView.setLayoutManager(gridLayoutManager);
			recyclerView.setHasFixedSize(true);
			recyclerView.addItemDecoration(new VerticalGridCardSpacingDecoration());
			recyclerView.setAdapter(adapter);

			// Many color models are shown on screen at once. The default recycled view pool size is
			// only 5, so we manually set the pool size to avoid constantly creating new views when
			// the colors are randomized
			recyclerView.getRecycledViewPool().setMaxRecycledViews(R.layout.model_group, 50);
		}

		private void setupClickListeners() {
			mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					queryGroupPost();
					mSwipeRefreshLayout.setRefreshing(false);
				}
			});

			fab = (FloatingActionButton) view.findViewById(R.id.fab_group);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					queryCommIDPost();
					showMyDialog(getContext());
				}
			});
		}

    private void queryGroupPost() {
        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        String API_TOKEN = "Bearer " + AccountService.Instance().mAuthToken;

        Log.d(TAG, "MeetupFragment AuthToken: " + API_TOKEN);

        Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://community-ci.herokuapp.com")
            .build();
        ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);
        Call<ResponseBody> call = service.apiEventPost(API_TOKEN, makeGroupQuery());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    final String body = response.body().string();

                    adapter.removeGroup();

                    if(!body.isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                try {
                                    JSONArray jsonEvents = new JSONObject(body).getJSONObject("data").getJSONObject("allGroups").getJSONArray("edges");
                                    for (int i = 0; i < jsonEvents.length(); ++i) {
                                        JSONObject group = jsonEvents.getJSONObject(i).getJSONObject("node");
																				adapter.addGroup(gson.fromJson(group.toString(), Group.class));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "Response Body is null");
                        Log.d(TAG, "Response Body: " + body);
                    }

                } catch(Exception e) {
                    Log.d(TAG, e.getMessage());
                    Log.d(TAG, "Events Body ERROR");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Log.d(TAG, "onFailure in enqueue");
            }
        });
    }

    private String makeGroupQuery() {
        return "{allGroups {\nedges{\nnode { title }}}}";
    }

	private void queryCommIDPost() {
		//Here a logging interceptor is created
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);

		//The logging interceptor will be added to the http client
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		httpClient.addInterceptor(logging);

		String API_TOKEN = "Bearer " + AccountService.Instance().mAuthToken;

		Log.d(TAG, "GroupFragment HashMap: " + API_TOKEN);

		Retrofit retrofit = new Retrofit.Builder()
				.client(httpClient.build())
				.addConverterFactory(GsonConverterFactory.create())
				.baseUrl("https://community-ci.herokuapp.com")
				.build();
		ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);
		Call<ResponseBody> call = service.apiEventPost(API_TOKEN, makeCommunityIDQuery());

		call.enqueue(new Callback<ResponseBody>() {
			@Override
			public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

				try {
					final String body = response.body().string();

					comm_options.clear();

					if(!body.isEmpty()) {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Gson gson = new Gson();
								try {
									JSONArray jsonEvents = new JSONObject(body).getJSONObject("data").getJSONObject("myCommunities").getJSONArray("edges");
									for (int i = 0; i < jsonEvents.length(); ++i) {
										JSONObject group = jsonEvents.getJSONObject(i).getJSONObject("node");
										comm_options.put(gson.fromJson(group.toString(), Community.class).getId(), gson.fromJson(group.toString(), Community.class).getTitle());

//										adapter.addGroup(gson.fromJson(group.toString(), Group.class));
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					} else {
						Log.d(TAG, "Response Body is null");
						Log.d(TAG, "Response Body: " + body);
					}

				} catch(Exception e) {
					Log.d(TAG, e.getMessage());
					Log.d(TAG, "Events Body ERROR");
				}
			}

			@Override
			public void onFailure(Call<ResponseBody> call, Throwable t) {
				Log.d(TAG, t.getMessage());
				Log.d(TAG, "onFailure in enqueue");
			}
		});
	}

	private String makeCommunityIDQuery() {
		return "{myCommunities {\nedges{\nnode { id, title, acronym }}}}";
	}

    private int getSpanCount() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 100);
    }

	private void showMyDialog(Context context) {
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_dialog);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);

		mProgressView = dialog.findViewById(R.id.login_progress);

		TextView textView = (TextView) dialog.findViewById(R.id.txtTitle);
		ListView listView = (ListView) dialog.findViewById(R.id.listView);
		Button btnBtmLeft = (Button) dialog.findViewById(R.id.btnBtmLeft);
		groupName = (EditText) dialog.findViewById(R.id.groupname);
		groupDescription = (EditText) dialog.findViewById(R.id.groupdescription);
		dropdown = (Spinner) dialog.findViewById(R.id.group_spinner);
		dropdown.setVisibility(View.INVISIBLE);
		ArrayAdapter<HashMap<String, String>> hashAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
		hashAdapter.add(comm_options);
		dropdown.setAdapter(hashAdapter);

		dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//				Toast.makeText(getContext(), parentView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		btnBtmLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mAuthTask = new AddGroupTask(groupName.getText().toString(), groupDescription.getText().toString()).execute();
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
	private class AddGroupTask extends AsyncTask<Void, Void, Boolean> {
		private Communicator communicator;

		private final String mTitle;
		private final String mDescription;


		AddGroupTask(String mTitle, String mDescription) {
			this.mTitle = mTitle;
			this.mDescription = mDescription;
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
				communicator.addGroupPost(mTitle, mDescription);

				successful = communicator.successful;

				Log.d(TAG, "ADD_GROUP_TASK_SUCCESSFUL: " + successful);
			} catch (Exception e) {
				Log.d("QUERY_POST_FAILURE", "THE QUERY WAS A FAILURE");

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
}
