package be.ugent.zeus.hydra.activities.minerva;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.viewpager.MinervaCoursePagerAdapter;

/**
 * Activity that displays a certain course.
 *
 * @author Niko Strijbol
 */
public class CourseActivity extends ToolbarActivity {

    public static final String ARG_COURSE = "argCourse";

    private static final String ONLINE_URL_DESKTOP = "https://minerva.ugent.be/main/course_home/course_home.php?cidReq=%s";
    private static final String ONLINE_URL_MOBILE = "https://minerva.ugent.be/mobile/courses/%s";

    private Course course;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minerva_course);

        Intent intent = getIntent();
        course = intent.getParcelableExtra(ARG_COURSE);

        TabLayout tabLayout = $(R.id.tab_layout);
        ViewPager viewPager = $(R.id.pager);

        viewPager.setAdapter(new MinervaCoursePagerAdapter(getSupportFragmentManager(), course));
        viewPager.setCurrentItem(1, false);
        tabLayout.setupWithViewPager(viewPager);

        setToolbarTitle(course.getTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.minerva_course_link:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getOnlineUrl())));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_minerva_course, menu);

        // We need to manually set the color of this Drawable for some reason.
        setWhiteIcons(menu, R.id.minerva_course_link);

        return true;
    }

    private String getOnlineUrl() {
        //TODO: use preferences
        return String.format(ONLINE_URL_DESKTOP, course.getId());
    }
}