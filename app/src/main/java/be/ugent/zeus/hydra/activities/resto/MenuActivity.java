package be.ugent.zeus.hydra.activities.resto;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.View;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.resto.common.RestoWebsiteActivity;
import be.ugent.zeus.hydra.loader.ThrowableEither;
import be.ugent.zeus.hydra.loader.cache.CacheRequest;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.recyclerview.adapters.resto.MenuPageAdapter;
import be.ugent.zeus.hydra.requests.resto.RestoMenuOverviewRequest;

import java.util.Collections;

/**
 * Display the menu of the resto in a separate view, similar to the old app.
 *
 * @author Niko Strijbol
 */
public class MenuActivity extends RestoWebsiteActivity<RestoOverview> {

    private static final String URL = "http://www.ugent.be/student/nl/meer-dan-studeren/resto";

    private MenuPageAdapter pageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto);
        
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pageAdapter = new MenuPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = $(R.id.resto_tabs_content);
        mViewPager.setAdapter(pageAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                HydraApplication app = (HydraApplication) MenuActivity.this.getApplication();
                app.sendScreenName("Menu tab: " + pageAdapter.getTabDate(position));
            }
        });

        TabLayout tabLayout = $(R.id.resto_tabs_slider);
        tabLayout.setupWithViewPager(mViewPager);

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