/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.resto.extrafood;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.databinding.ActivityExtraFoodBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class ExtraFoodActivity extends BaseActivity<ActivityExtraFoodBinding> {

    private ExtraFoodViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityExtraFoodBinding::inflate);

        ExtraFoodPagerAdapter adapter = new ExtraFoodPagerAdapter(this);
        binding.pager.setAdapter(adapter);
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout, binding.pager, adapter);
        mediator.attach();
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //noinspection DataFlowIssue
                var title = binding.tabLayout.getTabAt(position).getText().toString();
                Reporting.getTracker(ExtraFoodActivity.this)
                        .setCurrentScreen(
                                ExtraFoodActivity.this,
                                title,
                                FoodFragment.class.getSimpleName());
            }
        });

        viewModel = new ViewModelProvider(this).get(ExtraFoodViewModel.class);
        viewModel.refreshing().observe(this, aBoolean -> {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.tabLayout.clearOnTabSelectedListeners();
    }
}
