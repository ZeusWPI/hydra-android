package be.ugent.zeus.hydra.resto.menu;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.SingleDayFragment;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.threeten.bp.LocalDate;

import java.util.Collections;
import java.util.List;

/**
 * This class provides the tabs in the resto activity.
 *
 * @author Niko Strijbol
 */
class MenuPagerAdapter extends FragmentStatePagerAdapter {

    private List<RestoMenu> data = Collections.emptyList();
    private final Context context;

    MenuPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context.getApplicationContext();
    }

    public void setData(List<RestoMenu> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public boolean hasData() {
        return !data.isEmpty();
    }

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
     *
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