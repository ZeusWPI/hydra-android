package be.ugent.zeus.hydra.ui.main.minerva;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import be.ugent.zeus.hydra.ui.common.AdapterOutOfBoundsException;

/**
 * @author Niko Strijbol
 */
class MinervaPagerAdapter extends FragmentStatePagerAdapter {

    private boolean isLoggedIn;

    MinervaPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CourseFragment();
            case 1:
                return new AnnouncementsFragment();
            default:
                throw new AdapterOutOfBoundsException(position, getCount());
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Vakken";
            case 1:
                return "Ongelezen aankondigingen";
            default:
                throw new AdapterOutOfBoundsException(position, getCount());
        }
    }

    @Override
    public int getCount() {
        return isLoggedIn ? 2 : 0;
    }
}
