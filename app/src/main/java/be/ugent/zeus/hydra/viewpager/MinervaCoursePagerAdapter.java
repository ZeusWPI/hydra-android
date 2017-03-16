package be.ugent.zeus.hydra.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import be.ugent.zeus.hydra.fragments.minerva.CourseAgendaFragment;
import be.ugent.zeus.hydra.fragments.minerva.CourseAnnouncementFragment;
import be.ugent.zeus.hydra.fragments.minerva.CourseInfoFragment;
import be.ugent.zeus.hydra.data.models.minerva.Course;

/**
 * This provides the tabs in a minerva course.
 *
 * @author Niko Strijbol
 */
public class MinervaCoursePagerAdapter extends FragmentPagerAdapter {

    private final Course course;

    public MinervaCoursePagerAdapter(FragmentManager fm, Course course) {
        super(fm);
        this.course = course;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CourseInfoFragment.newInstance(course);
            case 1:
                return CourseAnnouncementFragment.newInstance(course);
            case 2:
                return CourseAgendaFragment.newInstance(course);
        }

        throw new IllegalTabException(position, getCount());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Info";
            case 1:
                return "Aankondigingen";
            case 2:
                return "Agenda";
        }

        throw new IllegalTabException(position, getCount());
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 3;
    }
}