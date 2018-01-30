package be.ugent.zeus.hydra.minerva.ui.course;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.ui.preferences.MinervaFragment;
import be.ugent.zeus.hydra.minerva.Course;
import be.ugent.zeus.hydra.utils.NetworkUtils;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Activity that displays a certain course.
 *
 * The activity will return a result if one or more announcements have been updated (marked as read). The result's
 * intent will contain a boolean called {@link #RESULT_ANNOUNCEMENT_UPDATED}.
 * True indicates at least one announcement has been updated, otherwise it is false.
 *
 * @author Niko Strijbol
 */
public class CourseActivity extends BaseActivity {

    public static final String ARG_COURSE = "argCourse";
    public static final String ARG_TAB = "argTab";

    public static final String RESULT_ANNOUNCEMENT_UPDATED = "be.ugent.zeus.hydra.result.minerva.course.announcement.read";

    @Retention(SOURCE)
    @IntDef({Tab.INFO, Tab.ANNOUNCEMENTS, Tab.AGENDA})
    public @interface Tab {
        int INFO = 0;
        int ANNOUNCEMENTS = 1;
        int AGENDA = 2;
    }

    private static final String ONLINE_URL_DESKTOP = "https://minerva.ugent.be/main/course_home/course_home.php?cidReq=%s";
    private static final String ONLINE_URL_MOBILE = "https://minerva.ugent.be/mobile/courses/%s";

    private Course course;

    /**
     * Start the activity for a result.
     *
     * @param starter The object starting the activity.
     * @param course The course.
     * @param tab Which tab to show.
     */
    public static void startForResult(ResultStarter starter, Course course, @Tab int tab) {
        Intent intent = new Intent(starter.getContext(), CourseActivity.class);
        intent.putExtra(ARG_COURSE, (Parcelable) course);
        intent.putExtra(ARG_TAB, tab);
        starter.startActivityForResult(intent, starter.getRequestCode());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minerva_course);

        Intent intent = getIntent();
        course = intent.getParcelableExtra(ARG_COURSE);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.pager);

        viewPager.setAdapter(new MinervaCoursePagerAdapter(getSupportFragmentManager(), this, course));

        viewPager.setCurrentItem(getIntent().getIntExtra(ARG_TAB, Tab.ANNOUNCEMENTS), false);
        tabLayout.setupWithViewPager(viewPager);

        getToolbar().setTitle(course.getTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.minerva_course_link:
                NetworkUtils.maybeLaunchBrowser(this, getOnlineUrl());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_minerva_course, menu);
        tintToolbarIcons(menu, R.id.minerva_course_link);
        return super.onCreateOptionsMenu(menu);
    }

    private String getOnlineUrl() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean(MinervaFragment.PREF_USE_MOBILE_URL, false)) {
            return String.format(ONLINE_URL_MOBILE, course.getId());
        } else {
            return String.format(ONLINE_URL_DESKTOP, course.getId());
        }
    }
}