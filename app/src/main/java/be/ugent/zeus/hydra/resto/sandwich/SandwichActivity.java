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

package be.ugent.zeus.hydra.resto.sandwich;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.viewpager2.widget.ViewPager2;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.databinding.ActivityExtraFoodBinding;
import be.ugent.zeus.hydra.resto.extrafood.FoodFragment;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Activity for the sandwiches.
 */
public class SandwichActivity extends BaseActivity<ActivityExtraFoodBinding> {

    public static final String URL = "https://www.ugent.be/student/nl/meer-dan-studeren/resto/broodjes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityExtraFoodBinding::inflate);

        SandwichPagerAdapter adapter = new SandwichPagerAdapter(this);
        binding.pager.setAdapter(adapter);
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout, binding.pager, adapter);
        mediator.attach();
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Reporting.getTracker(getApplicationContext())
                        .setCurrentScreen(
                                SandwichActivity.this,
                                binding.tabLayout.getTabAt(position).getText().toString(),
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.tabLayout.clearOnTabSelectedListeners();
    }
}
