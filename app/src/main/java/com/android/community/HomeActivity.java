package com.android.community;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.community.authentication.Communicator;
import com.android.community.fragment.EventFragment;
import com.android.community.fragment.HomeFragment;
import com.android.community.fragment.MeetupFragment;
import com.android.community.fragment.NotifFragment;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /* Search Fields */
    MaterialSearchView searchView;

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.action_About);
        final PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.action_Profile);
        final PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.action_Notif);
        final PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.action_signout);

        item3.withBadge("8").withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.colorPrimaryDark));

        item1.withIcon(GoogleMaterial.Icon.gmd_flare);
        item2.withIcon(GoogleMaterial.Icon.gmd_perm_identity);
        item3.withIcon(GoogleMaterial.Icon.gmd_language);
        item4.withIcon(GoogleMaterial.Icon.gmd_power_settings_new);

        ProfileDrawerItem profileItem = new ProfileDrawerItem().withName("Adam Croutworst").withEmail("mikepenz@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile2));

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_1)
                .addProfiles(profileItem)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {

                        AsyncTask mQuery = new QueryTask().execute();

                        return true;
                    }
                })
                .build();


        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        item4
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if(drawerItem == item4) {
                            Toast.makeText(getApplicationContext(), "Signout",
                                        Toast.LENGTH_SHORT).show();
                            new SignoutTask().execute();
                        }

                        return true;
                    }
                })
                .build();

        new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName("Communities"),
                        new SecondaryDrawerItem().withIdentifier(2).withName("University of Washington"),
                        new SecondaryDrawerItem().withIdentifier(3).withName("UW"),
                        new SecondaryDrawerItem().withIdentifier(4).withName("Apple")
                )
                .withDrawerGravity(Gravity.END)
                .append(drawer);

        drawer.addStickyFooterItem(new PrimaryDrawerItem().withName("Drawer Footer"));

        searchView = (MaterialSearchView) findViewById(R.id.search);
        searchView.setVoiceSearch(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));

                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        // Create the adapter that will return a fragment for each of the five
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.home_tab);
        tabLayout.getTabAt(1).setIcon(R.drawable.event_tab);
        tabLayout.getTabAt(2).setIcon(R.drawable.meetup_tab);
        tabLayout.getTabAt(3).setIcon(R.drawable.group_tab);
        tabLayout.getTabAt(4).setIcon(R.drawable.notif_tab);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "This is a floating action button", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }

    private void filterSearchFor(String query) {
    }

    private void searchFor(String query) {
    }

    private void doMySearch(String query) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SignoutTask extends AsyncTask<String, Void, Boolean> {
        private Communicator communicator;

        @Override
        protected Boolean doInBackground(String... params) { // params[0] = username; params[1] = password
            boolean successful;
            // Retrofit HTTP call to login

            // for debug worker thread
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            try {
                communicator = new Communicator();
                communicator.signoutPost();

                successful = communicator.successful;

                Log.d(TAG, "Signout isSuccessful: " + communicator.successful);
                Log.d(TAG, "SIGNOUTTASK_SUCCESSFUL: " + successful);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("SIGNOUT_POST_FAILURE", "THE SIGNOUT WAS A FAILURE");

                return false;
            }

            Log.d(TAG, "successful2: " + successful);
            return successful;
        }

        @Override
        protected void onPostExecute(final Boolean successful) {
            Log.d(TAG, "inside onPostExecute");

            if (successful) {
                Log.d(TAG, "inside onPostExecute isSuccessful: " + successful);

                Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Signout was not Successful!",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class QueryTask extends AsyncTask<String, Void, Boolean> {
        private Communicator communicator;
        public String email;

        @Override
        protected Boolean doInBackground(String... params) { // params[0] = username; params[1] = password
            boolean successful;
            // Retrofit HTTP call to login

            // for debug worker thread
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            try {
                communicator = new Communicator();
                communicator.queryPost();

                successful = communicator.successful;
                email = communicator.email;

                Log.d(TAG, "Query email: " + email);
                Log.d(TAG, "Query isSuccessful: " + communicator.successful);
                Log.d(TAG, "QUERYTASK_SUCCESSFUL: " + successful);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("QUERY_POST_FAILURE", "THE QUERY WAS A FAILURE");

                return false;
            }

            Log.d(TAG, "successful2: " + successful);
            return successful;
        }

        @Override
        protected void onPostExecute(final Boolean successful) {
            Log.d(TAG, "inside onPostExecute");

            if (successful) {
                Log.d(TAG, "inside onPostExecute isSuccessful: " + successful);

                Toast.makeText(getApplicationContext(), "Email: " + email,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Query was not Successful!",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new EventFragment();
                case 2:
                    return new MeetupFragment();
                case 3:
                    return new GroupFragment();
                case 4:
                    return new NotifFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
