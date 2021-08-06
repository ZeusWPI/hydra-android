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

package be.ugent.zeus.hydra.resto.menu;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.SingleDayFragment;

/**
 * This class provides the tabs in the resto activity.
 *
 * @author Niko Strijbol
 */
class MenuPagerAdapter extends FragmentStatePagerAdapter {

    private final Context context;
    private List<RestoMenu> data = Collections.emptyList();

    MenuPagerAdapter(FragmentManager fm, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context.getApplicationContext();
    }

    public void setData(List<RestoMenu> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public boolean hasData() {
        return !data.isEmpty();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new LegendFragment();
        } else {
            return SingleDayFragment.newInstance(data.get(position - 1));
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof LegendFragment) {
            return POSITION_UNCHANGED;
        } else {
            return POSITION_NONE;
        }
    }

    @Override
    public int getCount() {
        if (hasData()) {
            return data.size() + 1;
        } else {
            return data.size();
        }
    }

    @Nullable
    LocalDate getTabDate(int position) {
        return position == 0 ? null : data.get(position - 1).getDate();
    }

    /**
     * The position of a tab is the number of days from today the menu is for.
     * This gets the date from the activity, because at this point, it is not guaranteed
     * the fragments have a date already. The activity does already have the dates however,
     * or no fragments will be made.
     *
     * @param position Which position the tab is in.
     * @return The title.
     */
    @Override
    @NonNull
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.resto_tab_title_legend);
        } else {
            return DateUtils.getFriendlyDate(context, data.get(position - 1).getDate());
        }
    }
}
