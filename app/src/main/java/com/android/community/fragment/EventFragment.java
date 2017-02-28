package com.android.community.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.community.AccountService;
import com.android.community.DataAdapter;
import com.android.community.R;
import com.android.community.authentication.Communicator;
import com.android.community.authentication.ServerRequestInterface;
import com.android.community.deserializer.AccountDeserializer;
import com.android.community.deserializer.EventDeserializer;
import com.android.community.models.AccountRegistration;
import com.android.community.models.Event;
import com.android.community.models.EventResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventFragment extends Fragment {

    private static String TAG = "EventFragment";

    /* Initialize Views */
    private ViewGroup view;
    private RecyclerView recyclerView;
    private ArrayList<Event> data;
    private DataAdapter adapter;

    private Boolean successful = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (ViewGroup) inflater.inflate(R.layout.fragment_events, container, false);
        initViews();

        return view;
    }

    private void initViews() {
        recyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_view_events);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        queryEventPost();
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

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(EventResponse.class, new EventDeserializer())
            .create();

        Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://community-ci.herokuapp.com")
            .build();
        ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);
        Call<EventResponse> call = service.apiEventPost(API_TOKEN, makeEventQuery());

        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
//                EventResponse eventResponse = response.body();
//                data = new ArrayList<>(Arrays.asList(response.body().getEvent()));
//                adapter = new DataAdapter(data);
                ArrayList<Event> event = new ArrayList<>();

                event.add(new Event("Batman vs Superman","Movie","ARC"));
                event.add(new Event("Basketball","5v5","Basketball courts"));
                event.add(new Event("Prom","HighSchool","Cruise"));
                event.add(new Event("Pizza Party","PizzaTime","ARC"));
                event.add(new Event("WorkoutChallenge","Workout","Gym"));
                event.add(new Event("La La Land","Movie","ARC"));

                adapter = new DataAdapter(event);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Log.d(TAG, "onFailure in enqueue");
            }

        });
//        ArrayList<Event> event = new ArrayList<>();
//
//        event.add(new Event("Batman vs Superman","Movie","ARC"));
//        event.add(new Event("Basketball","5v5","Basketball courts"));
//        event.add(new Event("Prom","HighSchool","Cruise"));
//        event.add(new Event("Pizza Party","PizzaTime","ARC"));
//        event.add(new Event("WorkoutChallenge","Workout","Gym"));
//        event.add(new Event("La La Land","Movie","ARC"));
//
//        adapter = new DataAdapter(event);
//        recyclerView.setAdapter(adapter);
    }

    private String makeEventQuery() {
        return "{allEvents {\nedges{\nnode { title, description, location }}}}";
    }
}
