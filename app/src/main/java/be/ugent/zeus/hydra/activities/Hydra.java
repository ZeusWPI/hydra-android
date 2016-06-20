package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.recyclerview.adapters.SectionPagerAdapter;

/**
 * Main activity.
 */
public class Hydra extends ToolbarActivity {

    //The tab icons
    private static int[] icons = {
            R.drawable.ic_tabs_home,
            R.drawable.ic_tabs_schamper,
            R.drawable.ic_tabs_menu,
            R.drawable.ic_tabs_events,
            R.drawable.ic_tabs_news,
            R.drawable.ic_tabs_info,
            R.drawable.ic_tabs_minerva,
    };

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //This activity has no parent.
        hasParent(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mViewPager = $(R.id.pager);
        mViewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));

        //Enable this if you want the actionbar to reappear when the user scrolls between tabs.
//        final AppBarLayout appBarLayout = $(R.id.app_bar_layout);
//
//        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                appBarLayout.setExpanded(true);
//            }
//        });

        TabLayout tabLayout = $(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        for (int i = 0; i < icons.length; i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }

        // If the app was launched via a resto notification, open the resto tab.
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action != null && action.equals(getString(R.string.resto_action))){
            changeFragment(2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mf = getMenuInflater();
        mf.inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimpliiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set the current tab.
     */
    public void changeFragment(int fragment) {
        mViewPager.setCurrentItem(fragment, false);
    }
}