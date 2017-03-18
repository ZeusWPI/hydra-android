package be.ugent.zeus.hydra.ui.sko.overview;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.IllegalTabException;

/**
 * Adapter for the tabs in the SKO portion of the app.
 *
 * @author Niko Strijbol
 */
public class SkoPagerAdapter extends FragmentPagerAdapter {

    private final String[] titles;

    public SkoPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        titles = context.getResources().getStringArray(R.array.sko_tab_names);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TimelineFragment();
            case 1:
                return new LineupFragment();
            case 2:
                return new VillageFragment();
            case 3:
                return new MapFragment();
            default:
                throw new IllegalTabException(position, getCount());
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public static String[] names = {"feed", "lineup", "village", "map"};
}