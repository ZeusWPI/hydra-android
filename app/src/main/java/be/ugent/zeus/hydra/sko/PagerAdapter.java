package be.ugent.zeus.hydra.sko;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.AdapterOutOfBoundsException;
import be.ugent.zeus.hydra.sko.lineup.LineupFragment;

/**
 * Adapter for the tabs in the SKO portion of the app.
 *
 * @author Niko Strijbol
 */
class PagerAdapter extends FragmentPagerAdapter {

    private final String[] titles;

    PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        titles = context.getResources().getStringArray(R.array.sko_tab_names);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
//                return new TimelineFragment();
//            case 1:
                return new LineupFragment();
//            case 2:
//                return new ExhibitorFragment();
//            case 3:
//                return new MapFragment();
            default:
                throw new AdapterOutOfBoundsException(position, getCount());
        }
    }

    @Override
    public int getCount() {
//        return 4;
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}