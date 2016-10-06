package be.ugent.zeus.hydra.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import be.ugent.zeus.hydra.fragments.*;
import be.ugent.zeus.hydra.fragments.home.HomeFeedFragment;
import be.ugent.zeus.hydra.fragments.minerva.MinervaFragment;
import be.ugent.zeus.hydra.fragments.resto.RestoFragment;

/**
 * Adapter for the tabs on the main activity.
 *
 * @author silox
 */
public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFeedFragment();
            case 1:
                return new SchamperFragment();
            case 2:
                return new RestoFragment();
            case 3:
                return new ActivitiesFragment();
            case 4:
                return new NewsFragment();
            case 5:
                return new InfoFragment();
            case 6:
                return new MinervaFragment();
            default:
                return new ComingSoonFragment();
        }
    }

    @Override
    public int getCount() {
        return 7;
    }

    public static final String[] names = {"home", "schamper", "resto", "activities", "news", "info", "minerva"};
}