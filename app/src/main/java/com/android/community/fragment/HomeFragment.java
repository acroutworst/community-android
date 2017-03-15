package com.android.community.fragment;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.community.AccountService;
import com.android.community.PostAdapter;
import com.android.community.R;
import com.android.community.authentication.ServerRequestInterface;
import com.android.community.models.Account;
import com.android.community.models.ButtonModel;
import com.android.community.models.Post;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by adamc on 1/10/17.
 */

public class HomeFragment extends Fragment {

    private final String TAG = "HomeFragment";

    private View rootView;
    private ImageView send;

    private EditText messageText;


    /* Chat Example */
    private Random random;
    public static ArrayList<Post> messageList;
    public static ArrayList<Account> userList;
    public static PostAdapter adapter;
    ListView msgListView;
    ImageButton sendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        initViews();

        /* Close the keyboard */
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setupClickListeners();

        return rootView;
    }

    private void initViews() {
//        send = (ImageView) rootView.findViewById(R.id.message_send);
//        messageText = (EditText) rootView.findViewById(R.id.message_text);
        random = new Random();
        messageText = (EditText) rootView.findViewById(R.id.messageEditText);
        msgListView = (ListView) rootView.findViewById(R.id.msgListView);
        sendButton = (ImageButton) rootView.findViewById(R.id.sendMessageButton);

        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        messageList = new ArrayList<>();
        userList = new ArrayList<>();
        adapter = new PostAdapter(getActivity(), messageList, userList);
        msgListView.setAdapter(adapter);

        queryPostsPost();
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEventPost(messageText.getText().toString());
                queryPostsPost();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    private void createEventPost(String text){
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
        Call<ResponseBody> call = service.apiCreateMeetupPost(API_TOKEN, registerEvent(text));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    final String body = response.body().string();

                    if(!body.isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    boolean meetupCreated = new JSONObject(body).getJSONObject("data").getJSONObject("registerPost").getBoolean("ok");
                                    Log.d(TAG, "postCreated : " + meetupCreated);
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
                    Log.d(TAG, "Post Body ERROR");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t){
                Log.d(TAG, t.getMessage());
                Log.d(TAG, "Failed creating the post");
            }
        });
    }

    private String registerEvent(String text) {
        return String.format("mutation{\nregisterPost(community:\"%s\", text:\"%s\"){ok, post{id, text, user{ username }, community{ title, acronym }}}}", "Q29tbXVuaXR5Tm9kZTox", text);
    }

    private void queryPostsPost() {
        //Here a logging interceptor is created
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        String API_TOKEN = "Bearer " + AccountService.Instance().mAuthToken;

        Log.d(TAG, "HomeFragment AuthToken: " + API_TOKEN);

        Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://community-ci.herokuapp.com")
            .build();
        ServerRequestInterface service = retrofit.create(ServerRequestInterface.class);
        Call<ResponseBody> call = service.apiMeetupPost(API_TOKEN, makePostQuery());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    final String body = response.body().string();

                    adapter.remove();

                    if(!body.isEmpty()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = new Gson();
                                try {
                                    JSONArray jsonEvents = new JSONObject(body).getJSONObject("data").getJSONObject("allPosts").getJSONArray("edges");
                                    for (int i = 0; i < jsonEvents.length(); ++i) {
                                        JSONObject post = jsonEvents.getJSONObject(i).getJSONObject("node");
                                        JSONObject user = jsonEvents.getJSONObject(i).getJSONObject("node").getJSONObject("user");
                                        adapter.add(gson.fromJson(post.toString(), Post.class), gson.fromJson(user.toString(), Account.class));
                                    }
                                    adapter.notifyDataSetChanged();
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
                    Log.d(TAG, "Post Body ERROR");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Log.d(TAG, "onFailure in enqueue");
            }
        });
    }

    private String makePostQuery() {
        return "query{ allPosts{ edges{ node{ id\n createDate\n text\n active\n user{ username } } } } }";
    }
}
