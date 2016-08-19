package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.viewpager.SectionPagerAdapter;

/**
 * Main activity.
 */
public class Hydra extends ToolbarActivity {

    public static final String ARG_TAB = "argTab";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //This activity has no parent.
        hasParent(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ViewPager viewpager = $(R.id.pager);
        viewpager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));

        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                HydraApplication app = (HydraApplication) Hydra.this.getApplication();
                app.sendScreenName("Fragment tab: " + SectionPagerAdapter.names[position]);
            }
        });

        final AppBarLayout appBarLayout = $(R.id.app_bar_layout);
        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                appBarLayout.setExpanded(true);
            }
        });

        TabLayout tabLayout = $(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewpager);

        for (int i = 0; i < icons.length; i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }

        //Get start position
        int start = getIntent().getIntExtra(ARG_TAB, 0);
        viewpager.setCurrentItem(start, false);
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
}