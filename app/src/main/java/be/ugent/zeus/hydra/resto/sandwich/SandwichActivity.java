package be.ugent.zeus.hydra.resto.sandwich;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.viewpager.widget.ViewPager;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.databinding.ActivityExtraFoodBinding;
import be.ugent.zeus.hydra.resto.extrafood.FoodFragment;

/**
 * Activity for the sandwiches.
 */
public class SandwichActivity extends BaseActivity<ActivityExtraFoodBinding> {

    public static final String URL = "https://www.ugent.be/student/nl/meer-dan-studeren/resto/broodjes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityExtraFoodBinding::inflate);

        SandwichPagerAdapter adapter = new SandwichPagerAdapter(getSupportFragmentManager(), this);
        binding.pager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.pager);
        binding.pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Reporting.getTracker(getApplicationContext())
                        .setCurrentScreen(
                                SandwichActivity.this,
                                adapter.getPageTitle(position).toString(),
                                FoodFragment.class.getSimpleName());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.resto_show_website) {
            NetworkUtils.maybeLaunchBrowser(this, URL);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sandwhich, menu);
        tintToolbarIcons(menu, R.id.resto_show_website);
        return true;
    }
}
