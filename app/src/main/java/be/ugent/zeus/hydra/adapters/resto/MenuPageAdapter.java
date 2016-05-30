package be.ugent.zeus.hydra.adapters.resto;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import be.ugent.zeus.hydra.activities.resto.MenuActivity;
import be.ugent.zeus.hydra.fragments.resto.menu.MenuTabFragment;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * This class provides the tabs in the resto activity.
 *
 * @author Niko Strijbol
 */
public class MenuPageAdapter extends FragmentStatePagerAdapter {

    private int number = 0;
    private MenuActivity activity;

    public MenuPageAdapter(FragmentManager fm, MenuActivity activity) {
        super(fm);
        this.activity = activity;
    }

    public void setNumber(int number) {
        this.number = number;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a MenuTabFragment (defined as a static inner class below).
        return MenuTabFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return number;
    }

    /**
     * The position of a tab is the number of days from today the menu is for.
     * This gets the date from the activity, because at this point, it is not guaranteed
     * the fragments have a date already. The activity does already have the dates however,
     * or no fragments will be made.
     *
     * @param position Which position the tab is in.
     *
     * @return The title.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return DateUtils.getFriendlyDate(activity.getMenu(position).getDate());
    }
}