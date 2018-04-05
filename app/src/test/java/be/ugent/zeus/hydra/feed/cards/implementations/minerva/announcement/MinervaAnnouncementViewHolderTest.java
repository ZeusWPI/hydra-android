package be.ugent.zeus.hydra.feed.cards.implementations.minerva.announcement;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import be.ugent.zeus.hydra.minerva.announcement.Announcement;
import be.ugent.zeus.hydra.minerva.announcement.SingleAnnouncementActivity;
import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.course.singlecourse.CourseActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertNotEmpty;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class MinervaAnnouncementViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populateLessThanLimit() {
        View view = inflate(R.layout.home_minerva_announcement_card);
        MinervaAnnouncementViewHolder viewHolder = new MinervaAnnouncementViewHolder(view, adapter);
        Course course = generate(Course.class);
        List<Announcement> announcements =
                generate(Announcement.class, MinervaAnnouncementViewHolder.MAX_DISPLAYED)
                        .collect(Collectors.toList());
        MinervaAnnouncementsCard announcementsCard = new MinervaAnnouncementsCard(announcements, course);
        viewHolder.populate(announcementsCard);

        LinearLayout layout = view.findViewById(R.id.linear_layout);

        assertEquals(MinervaAnnouncementViewHolder.MAX_DISPLAYED, layout.getChildCount());
        assertChild(layout, layout.getChildCount(), announcements);
        assertCardView(view, course);
    }

    @Test
    public void populateMoreThanLimit() {
        View view = inflate(R.layout.home_minerva_announcement_card);
        MinervaAnnouncementViewHolder viewHolder = new MinervaAnnouncementViewHolder(view, adapter);
        Course course = generate(Course.class);
        List<Announcement> announcements =
                generate(Announcement.class, MinervaAnnouncementViewHolder.MAX_DISPLAYED + 1)
                        .collect(Collectors.toList());
        MinervaAnnouncementsCard announcementsCard = new MinervaAnnouncementsCard(announcements, course);
        viewHolder.populate(announcementsCard);

        LinearLayout layout = view.findViewById(R.id.linear_layout);

        assertEquals(MinervaAnnouncementViewHolder.MAX_DISPLAYED + 1, layout.getChildCount());
        assertChild(layout, layout.getChildCount() - 1, announcements);
        assertNotEmpty((TextView) layout.getChildAt(layout.getChildCount() - 1));
        assertCardView(view, course);
    }

    private static void assertChild(LinearLayout layout, int n, List<Announcement> announcements) {
        for (int i = 0; i < n; i++) {
            View child = layout.getChildAt(i);
            assertNotEmpty(child.findViewById(R.id.title));
            assertNotEmpty(child.findViewById(R.id.subtitle));

            assertTrue(child.hasOnClickListeners());
            child.performClick();
            Intent intent = new Intent(child.getContext(), SingleAnnouncementActivity.class);
            Intent actual = ShadowApplication.getInstance().getNextStartedActivity();
            assertEquals(intent.getComponent(), actual.getComponent());
            assertEquals(announcements.get(i), actual.getParcelableExtra(SingleAnnouncementActivity.ARG_ANNOUNCEMENT));
        }
    }

    private static void assertCardView(View view, Course course) {
        view.findViewById(R.id.card_view).performClick();
        Intent expected = new Intent(view.getContext(), CourseActivity.class);
        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());
        assertEquals(course, actual.getParcelableExtra(CourseActivity.ARG_COURSE));
        assertEquals(CourseActivity.Tab.ANNOUNCEMENTS, actual.getIntExtra(CourseActivity.ARG_TAB, -1));
    }
}