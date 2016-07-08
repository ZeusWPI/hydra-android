package be.ugent.zeus.hydra.activities.resto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.View;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.resto.common.RestoWebsiteActivity;
import be.ugent.zeus.hydra.fragments.resto.RestoFragment;
import be.ugent.zeus.hydra.loader.ThrowableEither;
import be.ugent.zeus.hydra.loader.cache.CacheRequest;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.viewpager.MenuPagerAdapter;
import be.ugent.zeus.hydra.requests.resto.RestoMenuOverviewRequest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import java.util.Collections;
import java.util.Date;

/**
 * Display the menu of the resto in a separate view, similar to the old app.
 *
 * @author Niko Strijbol
 */
public class MenuActivity extends RestoWebsiteActivity<RestoOverview> {

    public static final String ARG_DATE = "start_date";

    private static final String URL = "http://www.ugent.be/student/nl/meer-dan-studeren/resto";

    private MenuPagerAdapter pageAdapter;
    private ViewPager mViewPager;
    private Date startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto);
        
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pageAdapter = new MenuPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = $(R.id.resto_tabs_content);
        mViewPager.setAdapter(pageAdapter);

        final AppBarLayout appBarLayout = $(R.id.app_bar_layout);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                appBarLayout.setExpanded(true);
                HydraApplication app = (HydraApplication) MenuActivity.this.getApplication();
                app.sendScreenName("Menu tab: " + pageAdapter.getTabDate(position));
            }
        });

        TabLayout tabLayout = $(R.id.resto_tabs_slider);
        tabLayout.setupWithViewPager(mViewPager);

        Intent intent = getIntent();

        //Get the default start date
        Date start = new Date();
        if(DateTime.now().isAfter(DateTime.now().withHourOfDay(RestoFragment.CLOSING_HOUR))) {
            start = DateTime.now().plusDays(1).toDate();
        }
        startDate = new Date(intent.getLongExtra(ARG_DATE, start.getTime()));

        startLoader();
    }

    /**
     * @return The URL for the overflow button to display a website link.
     */
    @Override
    protected String getUrl() {
        return URL;
    }

    /**
     * This method is used to receive new data.
     *
     * @param data The new data.
     */
    @Override
    public void receiveData(@NonNull RestoOverview data) {
        pageAdapter.setData(data);
        for (int i = 0; i < data.size(); i++) {
            RestoMenu menu = data.get(i);
            //Set the tab to this day!
            if(DateTimeComparator.getDateOnlyInstance().compare(menu.getDate(), startDate) >= 0) {
                mViewPager.setCurrentItem(i, false);
                break;
            }
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.  The application
     * should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<ThrowableEither<RestoOverview>> loader) {
        super.onLoaderReset(loader);
        pageAdapter.setData(Collections.<RestoMenu>emptyList());
    }

    /**
     * @return The main view of this activity. Currently this is used for snackbars, but that may change.
     */
    @Override
    protected View getView() {
        return mViewPager;
    }

    @Override
    public CacheRequest<RestoOverview> getRequest() {
        return new RestoMenuOverviewRequest();
    }
}