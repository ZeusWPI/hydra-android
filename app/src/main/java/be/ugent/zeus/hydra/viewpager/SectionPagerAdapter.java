package be.ugent.zeus.hydra.viewpager;

import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import be.ugent.zeus.hydra.fragments.*;
import be.ugent.zeus.hydra.fragments.home.HomeFragment;
import be.ugent.zeus.hydra.fragments.minerva.MinervaFragment;
import be.ugent.zeus.hydra.fragments.resto.RestoFragment;
import be.ugent.zeus.hydra.fragments.urgent.UrgentFragment;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

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
            case HOME:
                return new HomeFragment();
            case SCHAMPER:
                return new SchamperFragment();
            case RESTO:
                return new RestoFragment();
            case ACTIVITIES:
                return new ActivitiesFragment();
            case NEWS:
                return new NewsFragment();
            case INFO:
                return new InfoFragment();
            case MINERVA:
                return new MinervaFragment();
            case URGENT:
                return new UrgentFragment();
            default:
                return new ComingSoonFragment();
        }
    }

    @Override
    public int getCount() {
        return names.length;
    }

    public static final String[] names = {"home", "schamper", "resto", "activities", "news", "info", "minerva", "urgent"};

    @Retention(SOURCE)
    @IntDef({HOME, SCHAMPER, RESTO, ACTIVITIES, NEWS, INFO, MINERVA, URGENT})
    public @interface Tabs{}

    public static final int HOME = 0;
    public static final int SCHAMPER = 1;
    public static final int RESTO = 2;
    public static final int ACTIVITIES = 3;
    public static final int NEWS = 4;
    public static final int INFO = 5;
    public static final int MINERVA = 6;
    public static final int URGENT = 7;
}