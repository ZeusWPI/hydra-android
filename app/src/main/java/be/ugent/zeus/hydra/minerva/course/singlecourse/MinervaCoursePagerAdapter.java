package be.ugent.zeus.hydra.minerva.course.singlecourse;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.course.Module;
import be.ugent.zeus.hydra.minerva.announcement.courselist.AnnouncementsForCourseFragment;
import be.ugent.zeus.hydra.minerva.calendar.upcominglist.UpcomingCalendarForCourseFragment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * This provides the tabs in a minerva course.
 *
 * @author Niko Strijbol
 */
class MinervaCoursePagerAdapter extends FragmentPagerAdapter {

    private final Course course;
    private final Context context;
    private final List<Module> modules;

    MinervaCoursePagerAdapter(FragmentManager fm, Context context, Course course) {
        super(fm);
        this.context = context.getApplicationContext();
        this.course = course;
        modules = new ArrayList<>();
        EnumSet<Module> enabled = course.getEnabledModules();
        if (enabled.contains(Module.ANNOUNCEMENTS)) {
            modules.add(Module.ANNOUNCEMENTS);
        }
        if (enabled.contains(Module.CALENDAR)) {
            modules.add(Module.CALENDAR);
        }
    }

    @Override
    public Fragment getItem(int position) {
        // Default is fixed.
        if (position == 0) {
            return CourseInfoFragment.newInstance(course);
        }

        requireModuleCount(position);
        switch (modules.get(position - 1)) {
            case ANNOUNCEMENTS:
                return AnnouncementsForCourseFragment.newInstance(course);
            case CALENDAR:
                return UpcomingCalendarForCourseFragment.newInstance(course);
            default:
                throw new IllegalStateException("Module not supported.");
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Default is fixed.
        if (position == 0) {
            return context.getString(R.string.minerva_course_details_tabs_overview);
        }

        requireModuleCount(position);
        switch (modules.get(position - 1)) {
            case ANNOUNCEMENTS:
                return context.getString(R.string.minerva_course_details_tabs_announcements);
            case CALENDAR:
                return context.getString(R.string.minerva_course_details_tabs_calendar);
            default:
                throw new IllegalStateException("Module not supported.");
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return modules.size() + 1;
    }

    private void requireModuleCount(int position) {
        // Check if position is in range.
        if (position - 1 > modules.size()) {
            throw new IllegalStateException("Not enough modules are enabled, expected at least " + (position - 1) + ", but got " + modules.size());
        }
    }
}