package be.ugent.zeus.hydra.activities.resto;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.resto.MenuPageAdapter;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.requests.RestoMenuOverviewRequest;

import java.util.ArrayList;

/**
 * Display the menu of the resto in a separate view, similar to the old app.
 *
 * @author Niko Strijbol
 */
public class MenuActivity extends RestoWebsiteActivity<RestoMenu> {

    private static final String DATA = "resto_data_list";
    private static final String URL = "http://www.ugent.be/student/nl/meer-dan-studeren/resto";

    private MenuPageAdapter pageAdapter;
    private ViewPager mViewPager;

    public RestoMenu getMenu(int position) {
        return data.get(position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_resto);
        super.onCreate(savedInstanceState);
        
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pageAdapter = new MenuPageAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = $(R.id.resto_tabs_content);
        assert mViewPager != null;
        mViewPager.setAdapter(pageAdapter);

        TabLayout tabLayout = $(R.id.resto_tabs_slider);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(mViewPager);

        initFromBundle(savedInstanceState);
    }

    /**
     * @return The URL for the overflow button to display a website link.
     */
    @Override
    protected String getUrl() {
        return URL;
    }

    /**
     * Request the menu DATA. Once the DATA is loaded, pass it to the {@link MenuPageAdapter}.
     *
     * @param force If true, the cache is cleared.
     */
    public void performRequest(final boolean force) {
        performRequest(force, new RestoMenuOverviewRequest());
    }

    /**
     * This method is used to receive new data.
     *
     * @param data The new data.
     */
    @Override
    public void receiveData(ArrayList<RestoMenu> data) {
        super.receiveData(data);
        pageAdapter.setNumber(data.size());
    }

    /**
     * @return The name of the saved data.
     */
    @Override
    protected String getDataName() {
        return DATA;
    }

    /**
     * @return The main view of this activity. Currently this is used for snackbars, but that may change.
     */
    @Override
    protected View getMainView() {
        return mViewPager;
    }
}