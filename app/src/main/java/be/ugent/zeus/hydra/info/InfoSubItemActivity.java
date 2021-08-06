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

package be.ugent.zeus.hydra.info;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.databinding.ActivityInfoSubItemBinding;

public class InfoSubItemActivity extends BaseActivity<ActivityInfoSubItemBinding> {

    public static final String INFO_TITLE = "be.ugent.zeus.hydra.infoTitle";
    public static final String INFO_ITEMS = "be.ugent.zeus.hydra.infoItems";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityInfoSubItemBinding::inflate);
        // Display the fragment as the main content.
        InfoFragment fragment = new InfoFragment();

        Intent intent = getIntent();

        // Set title
        String title = intent.getStringExtra(INFO_TITLE);
        requireToolbar().setTitle(title);

        // Create bundle for fragment
        ArrayList<InfoItem> infoList = intent.getParcelableArrayListExtra(INFO_ITEMS);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(INFO_ITEMS, infoList);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.info_sub_item, fragment).commit();
    }
}