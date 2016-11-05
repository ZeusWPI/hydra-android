package be.ugent.zeus.hydra.activities.resto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loaders.LoaderCallbackHandler;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.requests.resto.RestoMenuRequest;
import be.ugent.zeus.hydra.viewpager.MenuPagerAdapter;
import org.threeten.bp.LocalDate;

import java.util.Collections;

/**
 * Display the menu of the resto in a separate view, similar to the old app.
 *
 * @author Niko Strijbol
 */
public class MenuActivity extends RestoActivity<RestoOverview> implements LoaderCallbackHandler.ResetListener {

    public static final String ARG_DATE = "start_date";

    private static final String URL = "http://www.ugent.be/student/nl/meer-dan-studeren/resto";

    private MenuPagerAdapter pageAdapter;
    private ViewPager viewPager;
    private LocalDate startDate;

    public MenuActivity() {
        this.loaderHandler.setResetListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto);
        
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pageAdapter = new MenuPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = $(R.id.resto_tabs_content);
        viewPager.setAdapter(pageAdapter);

        final AppBarLayout appBarLayout = $(R.id.app_bar_layout);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                appBarLayout.setExpanded(true);
                HydraApplication app = (HydraApplication) MenuActivity.this.getApplication();
                app.sendScreenName("Menu tab: " + pageAdapter.getTabDate(position));
            }
        });

        TabLayout tabLayout = $(R.id.resto_tabs_slider);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();

        //Get the default start date
        if(intent.hasExtra(ARG_DATE)) {
            startDate = (LocalDate) intent.getSerializableExtra(ARG_DATE);
        } else {
            startDate = LocalDate.now();
        }

        loaderHandler.startLoader();
    }

    @Override
    protected String getUrl() {
        return URL;
    }

    @Override
    public void receiveData(@NonNull RestoOverview data) {
        pageAdapter.setData(data);
        for (int i = 0; i < data.size(); i++) {
            RestoMenu menu = data.get(i);
            //Set the tab to this day!
            if(menu.getDate().isEqual(startDate)) {
                viewPager.setCurrentItem(i, true);
                break;
            }
        }
    }

    @Override
    public RestoMenuRequest getRequest() {
        return new RestoMenuRequest();
    }

    @Override
    public void onLoaderReset() {
        pageAdapter.setData(Collections.emptyList());
    }
}