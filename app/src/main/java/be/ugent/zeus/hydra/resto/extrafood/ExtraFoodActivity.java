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
import be.ugent.zeus.hydra.databinding.ActivityExtraFoodBinding;
import com.google.android.material.tabs.TabLayout;

public class ExtraFoodActivity extends BaseActivity<ActivityExtraFoodBinding> {

    private ExtraFoodViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityExtraFoodBinding::inflate);

        ExtraFoodPagerAdapter adapter = new ExtraFoodPagerAdapter(getSupportFragmentManager(), this);
        binding.pager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.pager);
        binding.pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Reporting.getTracker(ExtraFoodActivity.this)
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
