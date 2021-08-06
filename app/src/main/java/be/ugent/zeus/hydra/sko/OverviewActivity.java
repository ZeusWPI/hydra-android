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

package be.ugent.zeus.hydra.sko;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.databinding.ActivitySkoMainBinding;

/**
 * SKO overview activity. Only displays the line-up.
 *
 * @author Niko Strijbol
 */
public class OverviewActivity extends BaseActivity<ActivitySkoMainBinding> {

    private static final String SKO_WEBSITE = Endpoints.SKO;

    /**
     * Start the activity on the default tab.
     *
     * @param context The context.
     * @return The intent to start the activity.
     */
    public static Intent start(Context context) {
        return new Intent(context, OverviewActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivitySkoMainBinding::inflate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sko, menu);
        tintToolbarIcons(menu, R.id.sko_visit_website);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sko_visit_website) {
            NetworkUtils.maybeLaunchBrowser(this, SKO_WEBSITE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
