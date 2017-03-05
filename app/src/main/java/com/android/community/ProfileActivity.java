package com.android.community;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.community.utils.CircleTransformation;
import com.android.community.views.RevealBackgroundView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;

public class ProfileActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener {
	public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

	private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
	private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

	RevealBackgroundView vRevealBackground;
	RecyclerView rvUserProfile;
	TabLayout tlUserProfileTabs;
	ImageView ivUserProfilePhoto;
	View vUserDetails;
	Button btnFollow;
	View vUserStats;
	View vUserProfileRoot;
	Toolbar toolbar;

	TextView profileName;
	TextView profileUsername;
	TextView profileEmail;

	private int avatarSize;
	private String profilePhoto;
	private ProfileAdapter userPhotosAdapter;

	public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity) {
		Intent intent = new Intent(startingActivity, ProfileActivity.class);
		intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
		startingActivity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		toolbar = (Toolbar) findViewById(R.id.toolbar);

		toolbar.setNavigationIcon(R.drawable.ic_back);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent homeIntent = new Intent(ProfileActivity.this, HomeActivity.class);
				startActivity(homeIntent);
				finish();
			}
		});

		profileName = (TextView) findViewById(R.id.profile_name);
		profileName.setText(String.format("%s %s", AccountService.Instance().mAccount.firstName, AccountService.Instance().mAccount.lastName));
		profileUsername = (TextView) findViewById(R.id.profile_username);
		profileUsername.setText(AccountService.Instance().mAccount.username);
		profileEmail = (TextView) findViewById(R.id.profile_email);
		profileEmail.setText(AccountService.Instance().mAccount.email);

		vRevealBackground = (RevealBackgroundView) findViewById(R.id.vRevealBackground);
		tlUserProfileTabs = (TabLayout) findViewById(R.id.tlUserProfileTabs);
		ivUserProfilePhoto = (ImageView) findViewById(R.id.ivUserProfilePhoto);
		rvUserProfile = (RecyclerView) findViewById(R.id.rvUserProfile);
		vUserDetails = findViewById(R.id.vUserDetails);
		btnFollow = (Button) findViewById(R.id.btnFollow);
		vUserStats = findViewById(R.id.vUserStats);
		vUserProfileRoot = findViewById(R.id.vUserProfileRoot);

		this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
		this.profilePhoto = getString(R.string.user_profile_photo);

		Picasso.with(this)
				.load(profilePhoto)
				.placeholder(R.drawable.img_circle_placeholder)
				.resize(avatarSize, avatarSize)
				.centerCrop()
				.transform(new CircleTransformation())
				.into(ivUserProfilePhoto);

		setupTabs();
		setupUserProfileGrid();
		setupRevealBackground(savedInstanceState);
	}

	private void setupTabs() {
		tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.home_tab));
		tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.event_tab));
		tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.meetup_tab));
		tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.group_tab));
	}

	private void setupUserProfileGrid() {
		final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
		rvUserProfile.setLayoutManager(layoutManager);
		rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				userPhotosAdapter.setLockedAnimations(true);
			}
		});
	}

	private void setupRevealBackground(Bundle savedInstanceState) {
		vRevealBackground.setOnStateChangeListener(ProfileActivity.this);
		if (savedInstanceState == null) {
			final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
			vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
					vRevealBackground.startFromLocation(startingLocation);
					return true;
				}
			});
		} else {
			vRevealBackground.setToFinishedFrame();
			userPhotosAdapter.setLockedAnimations(true);
		}
	}

	@Override
	public void onStateChange(int state) {
		if (RevealBackgroundView.STATE_FINISHED == state) {
			rvUserProfile.setVisibility(View.VISIBLE);
			tlUserProfileTabs.setVisibility(View.VISIBLE);
			vUserProfileRoot.setVisibility(View.VISIBLE);
			userPhotosAdapter = new ProfileAdapter(this);
			rvUserProfile.setAdapter(userPhotosAdapter);
			animateUserProfileOptions();
			animateUserProfileHeader();
		} else {
			tlUserProfileTabs.setVisibility(View.INVISIBLE);
			rvUserProfile.setVisibility(View.INVISIBLE);
			vUserProfileRoot.setVisibility(View.INVISIBLE);
		}
	}

	private void animateUserProfileOptions() {
		tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
		tlUserProfileTabs.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
	}

	private void animateUserProfileHeader() {
		vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
		ivUserProfilePhoto.setTranslationY(-ivUserProfilePhoto.getHeight());
		vUserDetails.setTranslationY(-vUserDetails.getHeight());
		vUserStats.setAlpha(0);

		vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
		ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
		vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
		vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
	}

}
