package be.ugent.zeus.hydra.activities.sko;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.viewpager.SkoPagerAdapter;

/**
 * SKO overview activity.
 *
 * @author Niko Strijbol
 */
public class OverviewActivity extends ToolbarActivity {

    private static final String SKO_WEBSITE = "http://www.studentkickoff.be/";

    public static final String ARG_TAB = "argTab";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sko_main);

        ViewPager viewpager = $(R.id.pager);
        viewpager.setAdapter(new SkoPagerAdapter(getSupportFragmentManager(), this));

        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                HydraApplication app = (HydraApplication) OverviewActivity.this.getApplication();
                app.sendScreenName("SKO tab: " + SkoPagerAdapter.names[position]);
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

        //Get start position
        int start = getIntent().getIntExtra(ARG_TAB, 0);
        viewpager.setCurrentItem(start, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sko, menu);
        tintToolbarIcons(menu, R.id.sko_visit_website);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sko_visit_website:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SKO_WEBSITE)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}