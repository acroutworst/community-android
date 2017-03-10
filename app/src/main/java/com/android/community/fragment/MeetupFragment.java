package com.android.community.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.community.AccountService;
import com.android.community.CardAdapter;
import com.android.community.R;
import com.android.community.authentication.ServerRequestInterface;
import com.android.community.models.Meetup;
import com.google.gson.Gson;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeetupFragment extends Fragment {
    final private String TAG = "MeetupFragment";

    private RecyclerView recyclerView;
    private ViewGroup view;
    private CardAdapter adapter;
    private FloatingActionButton fab;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (ViewGroup) inflater.inflate(R.layout.fragment_meetup, container, false);

        initView();
        setupClickListeners();
        queryMeetupPost();

        return view;
    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.meetup_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_meetup);

        adapter = new CardAdapter();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setRecycleChildrenOnDetach(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.getRecycledViewPool().setMaxRecycledViews(R.layout.model_meetup, 50);
    }

    private void setupClickListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryMeetupPost();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        fab = (FloatingActionButton) view.findViewById(R.id.fab_meetup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddMeetupDialog(getContext());
            }
        });
    }

    private void showAddMeetupDialog(Context context){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_meetup_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        final TextView mMeetupTitle = (TextView) dialog.findViewById(R.id.meetup_name);
        final TextView mMeetupDescription = (TextView) dialog.findViewById(R.id.meetup_description);

        final CheckBox mPrivateMeetupCheckBox = (CheckBox) dialog.findViewById(R.id.meetup_private_checkbox);
        if(mPrivateMeetupCheckBox.isChecked()){
            mPrivateMeetupCheckBox.setChecked(true);
        } else{
            mPrivateMeetupCheckBox.setChecked(false);
        }

        final MaterialBetterSpinner mMeetupSpinner = (MaterialBetterSpinner) dialog.findViewById(R.id.meetup_spinner);
        // TODO: get a list of communities and put them in String[]
        // Current elements are {UWB, The Community, Microsoft}
        String[] items = new String[]{"Q29tbXVuaXR5Tm9kZTox", "Q29tbXVuaXR5Tm9kZToy", "Q29tbXVuaXR5Tm9kZTo1"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, items);
        mMeetupSpinner.setAdapter(adapter);

        final Button mAddMeetupButton = (Button) dialog.findViewById(R.id.add_meetup_btn);

        mAddMeetupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                // Variables for error checking purposes
                View focusView = null;
                boolean cancel = false;

                // Grabbing info from View classes
                String meetupTitle = mMeetupTitle.getText().toString();
                String meetupDescription = mMeetupDescription.getText().toString();
                boolean privateMeetup = mPrivateMeetupCheckBox.isChecked();
                // TODO: change duration's value to match design
                int duration = 3600;
                String meetupInCommunity = mMeetupSpinner.getText().toString();

                // Error checking
                if(TextUtils.isEmpty(meetupTitle)){
                    mMeetupTitle.setError(getString(R.string.error_meetup_name_required));
                    focusView = mMeetupTitle;
                    cancel = true;
                }

                if(TextUtils.isEmpty(meetupInCommunity)){
                    mMeetupSpinner.setError(getString(R.string.error_community_for_meetup_required));
                    focusView = mMeetupSpinner;
                    cancel = true;
                }

                // If Non-null fields are not null, create the meetup. Otherwise invoke error message and UI
                if(cancel){
                    focusView.requestFocus();
                } else{
                    createMeetupPost(meetupTitle, meetupDescription, privateMeetup, duration, meetupInCommunity); // HTTP call to create a new meetup M for a certain community C
                    dialog.dismiss();
                }
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

    private void createMeetupPost(String meetupTitle, String meetupDescription, boolean privateMeetup, int duration, String communityId){
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
        Call<ResponseBody> call = service.apiCreateMeetupPost(API_TOKEN, makeMeetupMutation(meetupTitle, meetupDescription, privateMeetup, duration, communityId));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    final String body = response.body().string();

                    adapter.removeMeetup();

                    if(!body.isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    boolean meetupCreated = new JSONObject(body).getJSONObject("data").getJSONObject("registerMeetup").getBoolean("ok");
                                    Log.d(TAG, "meetupCreated : " + meetupCreated);

                                    queryMeetupPost();
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
                    Log.d(TAG, "Meetups Body ERROR");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t){
                Log.d(TAG, t.getMessage());
                Log.d(TAG, "Failed creating the meetup");
            }
        });
    }

    private String makeMeetupMutation(String meetupTitle, String meetupDescription, boolean privateMeetup, int duration, String communityId) {
        return String.format("mutation{ registerMeetup(name:\"%s\", description:\"%s\", private:%s, duration:%s, community:\"%s\"){ ok } }",
                meetupTitle, meetupDescription, privateMeetup, duration, communityId);
    }

    private void queryMeetupPost() {
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
        Call<ResponseBody> call = service.apiEventPost(API_TOKEN, makeMeetupQuery());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    final String body = response.body().string();

                    adapter.removeMeetup();

                    if(!body.isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                try {
                                    JSONArray jsonEvents = new JSONObject(body).getJSONObject("data").getJSONObject("allMeetups").getJSONArray("edges");
                                    for (int i = 0; i < jsonEvents.length(); ++i) {
                                        JSONObject meetup = jsonEvents.getJSONObject(i).getJSONObject("node");
                                        adapter.addMeetup(gson.fromJson(meetup.toString(), Meetup.class));
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
                    Log.d(TAG, "Meetups Body ERROR");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Log.d(TAG, "onFailure in enqueue");
            }
        });
    }

    private String makeMeetupQuery() {
        return "{allMeetups {\nedges{\nnode { name, description }}}}";
    }
}