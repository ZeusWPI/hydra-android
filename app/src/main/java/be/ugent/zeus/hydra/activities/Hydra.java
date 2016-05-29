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
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.SectionPagerAdapter;
import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;


public class Hydra extends AppCompatActivity {

    protected TabLayout tabLayout;
    protected ViewPager viewPager;
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

    protected void onResume() {
        super.onResume();

        HydraApplication happ = (HydraApplication) getApplication();
        happ.sendScreenName("settings");
    }

    //------------------------------------------------------------------------
    //---------end of block that can fit in a common base class for all activities
    //------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Hydra_Main_Front);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setCustomView(R.layout.actionbar_centered_hydra);

        //icons (bad way)
        int[] icons = {R.drawable.home, R.drawable.minerva,
                R.drawable.resto, R.drawable.association_activities_icon, R.drawable.info};

        //set icons
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
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

    public void changeFragment(int fragment) { // FIXME: 14/04/16 Add more robust way to change fragments
        viewPager.setCurrentItem(fragment);

    }
}
