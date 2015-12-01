package be.ugent.zeus.hydra.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import be.ugent.zeus.hydra.fragments.ActivitiesFragment;
import be.ugent.zeus.hydra.fragments.HomeFragment;
import be.ugent.zeus.hydra.fragments.MinervaFragment;

/**
 * Created by silox on 17/10/15.
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
                return new MinervaFragment();
            default:
                return new ActivitiesFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "First Tab";
            case 1:
            default:
                return "Second Tab";
        }
    }

}
