package be.ugent.zeus.hydra.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import be.ugent.zeus.hydra.fragments.ActivitiesFragment;
import be.ugent.zeus.hydra.fragments.ComingSoonFragment;
import be.ugent.zeus.hydra.fragments.HomeFragment;
import be.ugent.zeus.hydra.fragments.InfoFragment;
import be.ugent.zeus.hydra.fragments.resto.RestoFragment;
import be.ugent.zeus.hydra.fragments.SchamperFragment;

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
                return new HomeFragment();
            case 1:
                return new SchamperFragment();
            case 2:
                return new RestoFragment();
            case 3:
                return new ActivitiesFragment();
            case 4:
                return new InfoFragment();
            default:
                return new ComingSoonFragment();
        }
    }

    @Override
    public int getCount() {
        return 6;
    }
}