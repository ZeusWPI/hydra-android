package be.ugent.zeus.hydra.resto.sandwich;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.AdapterOutOfBoundsException;
import be.ugent.zeus.hydra.resto.sandwich.ecological.EcologicalFragment;
import be.ugent.zeus.hydra.resto.sandwich.regular.RegularFragment;

/**
 * This provides the tabs in the sandwich overview.
 *
 * @author Niko Strijbol
 */
class SandwichPagerAdapter extends FragmentPagerAdapter {

    private final Context context;

    SandwichPagerAdapter(FragmentManager fm, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context.getApplicationContext();
    }

    @Override
    @NonNull
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RegularFragment();
            case 1:
                return new EcologicalFragment();
            default:
                throw new AdapterOutOfBoundsException(position, getCount());
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        @StringRes int string;
        switch (position) {
            case 0:
                string = R.string.resto_main_view_sandwiches_regular;
                break;
            case 1:
                string = R.string.resto_main_view_sandwiches_ecological;
                break;
            default:
                throw new AdapterOutOfBoundsException(position, getCount());
        }

        return context.getString(string);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 2;
    }
}
