package com.android.community.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.ListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.community.AccountService;
import com.android.community.CardAdapter;
import com.android.community.DataAdapter;
import com.android.community.R;
import com.android.community.authentication.ServerRequestInterface;
import com.android.community.models.Community;
import com.android.community.models.Event;
import com.android.community.models.Meetup;
import com.google.gson.Gson;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//List view: (views}
public class NotifFragment extends Fragment {

    private static String TAG = "CommunityFragment";


    //Array of communities:

    String[] communities = {};

    private RecyclerView recyclerView;
    private ViewGroup view;
    private DataAdapter adapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        view = (ViewGroup) inflater.inflate(R.layout.fragment_notif, container, false);

        initViews();
        setupClickListeners();
        return view;
    }

    //private void setupClickListeners(){
    //   queryCommunityPost();
    //}


    private void initViews()
    {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.md_black_1000),
                getResources().getColor(R.color.md_black_1000),
                getResources().getColor(R.color.md_blue_grey_500),
                getResources().getColor(R.color.md_deep_purple_100));

        recyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_view_communities);
        adapter = new DataAdapter();
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager((this.getContext()));
        recyclerView.setLayoutManager(layoutManager);
        queryCommunityPost();
    }


    private void setupClickListeners() {
        queryCommunityPost();
    }


    private void queryCommunityPost(){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        String API_TOKEN = "Bearer " + AccountService.Instance().mAuthToken;

        Log.d(TAG, "Community AuthToken: " + API_TOKEN);

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://community-ci.herokuapp.com")
                .build();
        ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);
        Call<ResponseBody> call = service.apiEventPost(API_TOKEN, makeCommunityQuery());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    final String body = response.body().string();



                    if(!body.isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                try {
                                    JSONArray jsonEvents = new JSONObject(body).getJSONObject("data").getJSONObject("allCommunities").getJSONArray("edges");
                                    for (int i = 0; i < jsonEvents.length(); ++i) {
                                        JSONObject communities = jsonEvents.getJSONObject(i).getJSONObject("node");
                                        adapter.addCommunity(gson.fromJson(communities.toString(), Community.class));
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

    private String makeCommunityQuery() {

        return "{myCommunities {\nedges{\nnode { id, title, acronym }}}}";
    }



}



