package com.croutworst.community;

import android.graphics.Color;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.action_About);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.action_Profile);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.action_Notif);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.action_signout);

        item3.withBadge("8").withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.colorPrimaryDark));

        item1.withIcon(GoogleMaterial.Icon.gmd_flare);
        item2.withIcon(GoogleMaterial.Icon.gmd_perm_identity);
        item3.withIcon(GoogleMaterial.Icon.gmd_language);
        //item3.withIcon(GoogleMaterial.Icon.gmd_error_outline);
        item4.withIcon(GoogleMaterial.Icon.gmd_power_settings_new);

        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        item4
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item
                        switch(position) {
//                            case 0:
//                                Toast.makeText(getApplicationContext(), "About page",
//                                        Toast.LENGTH_SHORT).show();
//                            case 1:
//                                Toast.makeText(getApplicationContext(), "Profile",
//                                        Toast.LENGTH_SHORT).show();
//                            case 2:
//                                Toast.makeText(getApplicationContext(), "Notifications!",
//                                        Toast.LENGTH_SHORT).show();
//                            case 3:
//                                Toast.makeText(getApplicationContext(), "Sign out?",
//                                        Toast.LENGTH_SHORT).show();
                            default:
                            Toast.makeText(getApplicationContext(), "This will go to Settings",
                                    Toast.LENGTH_SHORT).show();
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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This is a floating action button", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
