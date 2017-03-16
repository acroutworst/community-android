package com.android.community;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.android.community.authentication.ServerRequestInterface;
import com.android.community.models.Community;
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
import timber.log.Timber;

public class CommunityActivity extends AppCompatActivity {

	private static String TAG = "CommunityActivity";

	Toolbar toolbar;

	private RecyclerView recyclerView;
	private CommunityAdapter adapter;
	private ArrayList<Community> communities;

	private SwipeRefreshLayout mSwipeRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_community);

		initViews();
		setupClickListeners();
	}

	private void initViews() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_back);

		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.community_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.md_black_1000),
				getResources().getColor(R.color.md_black_1000),
				getResources().getColor(R.color.md_blue_grey_500),
				getResources().getColor(R.color.md_deep_purple_100));
		recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view_communities);
		communities = new ArrayList<>();
		adapter = new CommunityAdapter();
		recyclerView.setAdapter(adapter);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);

		queryCommunityPost();
	}

	private void setupClickListeners() {
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent homeIntent = new Intent(CommunityActivity.this, HomeActivity.class);
				startActivity(homeIntent);
				finish();
			}
		});

		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				queryCommunityPost();
				mSwipeRefreshLayout.setRefreshing(false);
			}
		});
	}

	private void queryCommunityPost(){

		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);

		//The logging interceptor will be added to the http client
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		httpClient.addInterceptor(logging);

		String API_TOKEN = "Bearer " + AccountService.Instance().mAuthToken;

		Timber.d("Community AuthToken: " + API_TOKEN);

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

					adapter.removeCommunity();

					if(!body.isEmpty()) {
						runOnUiThread(new Runnable() {
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

	private String makeCommunityQuery() {
		return "{allCommunities {\nedges{\nnode { id, title, acronym }}}}";
	}
}
