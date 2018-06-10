package be.ugent.zeus.hydra.minerva.mainui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.AdapterOutOfBoundsException;
import be.ugent.zeus.hydra.minerva.announcement.unreadlist.UnreadAnnouncementsFragment;
import be.ugent.zeus.hydra.minerva.course.list.CourseListFragment;

/**
 * @author Niko Strijbol
 */
class MinervaPagerAdapter extends FragmentStatePagerAdapter {

    private boolean isLoggedIn;
    private Context context;

    MinervaPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context.getApplicationContext();
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
                return context.getString(R.string.minerva_main_tab_courses);
            case 1:
                return context.getString(R.string.minerva_main_tab_announcements);
            default:
                throw new AdapterOutOfBoundsException(position, getCount());
        }
    }

    @Override
    public int getCount() {
        return isLoggedIn ? 2 : 0;
    }
}
