package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.SectionPagerAdapter;
import be.ugent.zeus.hydra.common.activities.ToolbarActivity;

/**
 * Main activity.
 */
public class Hydra extends ToolbarActivity {

    //The tab icons
    private static int[] icons = {
            R.drawable.home,
            R.drawable.minerva,
            R.drawable.resto,
            R.drawable.association_activities_icon,
            R.drawable.info
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

        TabLayout tabLayout = $(R.id.tab_layout);
        assert tabLayout != null;

        tabLayout.setupWithViewPager(mViewPager);

        //set icons
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