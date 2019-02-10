package be.ugent.zeus.hydra.resto.extrafood;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.AdapterOutOfBoundsException;

/**
 * This provides the tabs in a minerva course.
 *
 * @author Niko Strijbol
 */
class ExtraFoodPagerAdapter extends FragmentPagerAdapter {

    private final Context context;

    ExtraFoodPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context.getApplicationContext();
    }

    @Override
    public Fragment getItem(int position) {
        return FoodFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        @StringRes int string;
        switch (position) {
            case 0:
                string = R.string.resto_extra_breakfast;
                break;
            case 1:
                string = R.string.resto_extra_desserts;
                break;
            case 2:
                string = R.string.resto_extra_drinks;
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
        return 3;
    }
}