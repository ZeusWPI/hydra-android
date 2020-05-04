package be.ugent.zeus.hydra.resto.extrafood;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import com.google.android.material.tabs.TabLayout;

public class ExtraFoodActivity extends BaseActivity {

    private ExtraFoodViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_food);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.pager);

        ExtraFoodPagerAdapter adapter = new ExtraFoodPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //noinspection ConstantConditions
                Reporting.getTracker(getApplicationContext())
                        .setCurrentScreen(
                                ExtraFoodActivity.this,
                                adapter.getPageTitle(position).toString(),
                                FoodFragment.class.getSimpleName());
            }
        });

        viewModel = new ViewModelProvider(this).get(ExtraFoodViewModel.class);
        viewModel.getRefreshing().observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                Toast.makeText(getApplicationContext(), R.string.resto_extra_refresh_started, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            viewModel.onRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        tintToolbarIcons(menu, R.id.action_refresh);
        return true;
    }
}
