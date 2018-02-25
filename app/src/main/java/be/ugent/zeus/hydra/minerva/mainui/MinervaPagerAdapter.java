package be.ugent.zeus.hydra.minerva.mainui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import be.ugent.zeus.hydra.common.ui.AdapterOutOfBoundsException;
import be.ugent.zeus.hydra.minerva.announcement.unreadlist.UnreadAnnouncementsFragment;
import be.ugent.zeus.hydra.minerva.course.list.CourseListFragment;

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
                return new CourseListFragment();
            case 1:
                return new UnreadAnnouncementsFragment();
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
