package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.SectionPagerAdapter;
import be.ugent.zeus.hydra.models.association.AssociationActivities;
import be.ugent.zeus.hydra.models.association.AssociationActivity;
import be.ugent.zeus.hydra.models.association.AssociationNews;
import be.ugent.zeus.hydra.models.association.AssociationNewsItem;
import be.ugent.zeus.hydra.requests.AssociationNewsRequest;


public class Hydra extends AppCompatActivity {

    //------------------------------------------------------------------------
    //this block can be pushed up into a common base class for all activities
    //------------------------------------------------------------------------

    //if you use a pre-set service,
    //use JacksonSpringAndroidSpiceService.class instead of JsonSpiceService.class
    protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);


    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    //------------------------------------------------------------------------
    //---------end of block that can fit in a common base class for all activities
    //------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setCustomView(R.layout.actionbar_centered_hydra);

        //icons (bad way)
        int[] icons = {R.drawable.home, R.drawable.minerva,
                R.drawable.resto, R.drawable.schamper, R.drawable.info};

        //set icons
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }

        //performLoadActivityRequest();
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

    private void performLoadActivityRequest() {
        /*AssociationActivitiesRequest r = new AssociationActivitiesRequest();
        System.out.println("Load data");
        spiceManager.execute(r, r.getCacheKey(), DurationInMillis.ONE_MINUTE * 15, new AssociationActivityRequestListener() );*/

        AssociationNewsRequest r = new AssociationNewsRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<AssociationNews>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                //TextView groenten = (TextView) findViewById(R.id.groenten);
                //groenten.setText("request failed");
                System.out.println("Request failed");
            }

            @Override
            public void onRequestSuccess(AssociationNews associationNewsItems) {
                for (AssociationNewsItem newsItem: associationNewsItems) {
                    System.out.println(newsItem.title + ", ");
                }
            }
        });
    }

    private class AssociationActivityRequestListener implements RequestListener<AssociationActivities> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            System.out.println("Request failed");
        }

        @Override
        public void onRequestSuccess(AssociationActivities associationActivities) {
            System.out.println("Activities loaded: " + associationActivities.size());
            for (AssociationActivity activity: associationActivities) {
                System.out.print(activity.title + ",  ");
            }
            System.out.println();
        }
    }
}
