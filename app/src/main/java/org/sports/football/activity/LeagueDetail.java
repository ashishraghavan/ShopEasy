package org.sports.football.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.sports.football.R;
import org.sports.football.fragment.Fixtures;
import org.sports.football.fragment.LeagueTable;
import org.sports.football.fragment.News;
import org.sports.football.fragment.Teams;
import org.sports.football.model.League;
import org.sports.football.util.Constants;

import java.util.List;

public class LeagueDetail extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private League league;
    private PagerSlidingTabStrip leagueDetailTabs;
    /*Maintain a list of fragments being used */
    private final List<Fragment> childFragments = Lists.newArrayList();
    /*Maintain a map of tab titles */
    private final List<String> tabtitleList = ImmutableList.of("News","Teams","Table","Fixtures");

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if ( getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.league_detail_viewpager);
        leagueDetailTabs = (PagerSlidingTabStrip)findViewById(R.id.league_detail_tabs);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Get the serialized league
        if ( !getIntent().hasExtra(Constants.SERIALIZABLE_LEAGUE) ||
                getIntent().getSerializableExtra(Constants.SERIALIZABLE_LEAGUE) == null ) {
            Snackbar.make(findViewById(R.id.main_content),"An error occured while loading data",Snackbar.LENGTH_LONG).show();
            return;
        }

        league = (League)getIntent().getSerializableExtra(Constants.SERIALIZABLE_LEAGUE);
        if ( league == null ) {
            Snackbar.make(findViewById(R.id.main_content),"An error occured while loading data",Snackbar.LENGTH_LONG).show();
            return;
        }
        setViewPagerTabs();
    }

    private void setViewPagerTabs() {
        Fragment newsFragment = News.newInstance();
        childFragments.add(0, newsFragment);

        Fragment teamsFragment = Teams.newInstance();
        childFragments.add(1, teamsFragment);

        Fragment tableFragment = LeagueTable.newInstance();
        childFragments.add(2, tableFragment);

        Fragment fixturesFragment = Fixtures.newInstance();
        childFragments.add(3, fixturesFragment);

        leagueDetailTabs.setShouldExpand(true);
        leagueDetailTabs.setAllCaps(false);

        leagueDetailTabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        leagueDetailTabs.setTextColor(Color.WHITE);
        leagueDetailTabs.setIndicatorColor(Color.WHITE);

        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        leagueDetailTabs.setViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_league_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return childFragments.get(position);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabtitleList.get(position);
        }
    }
}
