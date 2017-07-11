package be.ugent.zeus.hydra.ui.main.minerva;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.ui.common.IllegalTabException;

/**
 * @author Niko Strijbol
 */
public class MinervaPagerAdapter extends FragmentStatePagerAdapter {

    private boolean isLoggedIn;

    public MinervaPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return new MinervaFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Vakken";
            case 1:
                return "Aankondigingen";
            default:
                throw new IllegalTabException(position, getCount());
        }
    }

    @Override
    public int getCount() {
        return isLoggedIn ? 2 : 0;
    }
}
