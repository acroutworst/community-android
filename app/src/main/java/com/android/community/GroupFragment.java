package com.android.community;

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

import com.android.community.authentication.ServerRequestInterface;
import com.android.community.models.Event;
import com.android.community.models.Group;
import com.google.gson.Gson;

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


public class GroupFragment extends Fragment {
    final private String TAG = "GroupFragment";

		/* Initialize Views */
    private RecyclerView recyclerView;
    private ViewGroup view;
    private GridAdapter adapter;
		private SwipeRefreshLayout mSwipeRefreshLayout;
		private FloatingActionButton fab;


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
					queryGroupPost();
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

    private int getSpanCount() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 100);
    }
}
