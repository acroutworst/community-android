package com.android.community.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.community.AccountService;
import com.android.community.CardAdapter;
import com.android.community.DataAdapter;
import com.android.community.R;
import com.android.community.authentication.ServerRequestInterface;
import com.android.community.models.Event;
import com.android.community.models.Meetup;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by adamc on 1/10/17.
 */

public class HomeFragment extends Fragment {

    private static String TAG = "HomeFragment";

    /* Initialize Views */
    private ViewGroup view;
    private RecyclerView recyclerView;
    private ArrayList<Event> data;
    private DataAdapter adapter;
    private CardAdapter cardAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton fab;

    private Boolean successful = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);

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
        recyclerView = (RecyclerView) view.findViewById(R.id.home_recycler_view_events);
        recyclerView.setHasFixedSize(true);
        adapter = new DataAdapter();
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
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

        Log.d(TAG, "EventFragment AuthToken: " + API_TOKEN);

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

    private String makeEventQuery() {
        return "{allEvents {\nedges{\nnode { title, description, location }}}}";
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

                    cardAdapter.removeMeetup();

                    if(!body.isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                try {
                                    JSONArray jsonEvents = new JSONObject(body).getJSONObject("data").getJSONObject("allMeetups").getJSONArray("edges");
                                    for (int i = 0; i < jsonEvents.length(); ++i) {
                                        JSONObject meetup = jsonEvents.getJSONObject(i).getJSONObject("node");
                                        cardAdapter.addMeetup(gson.fromJson(meetup.toString(), Meetup.class));
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
