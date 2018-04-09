package be.ugent.zeus.hydra.minerva.course.list;

import android.content.Intent;
import android.os.SystemClock;
import android.support.transition.Visibility;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.SearchHelper;
import be.ugent.zeus.hydra.common.ui.recyclerview.ordering.OnStartDragListener;
import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.course.singlecourse.CourseActivity;
import java8.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;

import java.util.Arrays;
import java.util.Collection;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertNotEmpty;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * @author Niko Strijbol
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
public class MinervaCourseViewHolderTest {

    @ParameterizedRobolectricTestRunner.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true, true},
                {true, false},
                {false, true},
                {false, false}
        });
    }

    // Parameters
    private final boolean isSearching;
    private final boolean hasUnread;

    private OnStartDragListener listener;
    private SearchHelper helper;
    private ResultStarter starter;

    public MinervaCourseViewHolderTest(boolean isSearching, boolean hasUnread) {
        this.isSearching = isSearching;
        this.hasUnread = hasUnread;
    }

    @Before
    public void setUp() {
        listener = mock(OnStartDragListener.class);
        helper = mock(SearchHelper.class);
        starter = mock(ResultStarter.class);
        doAnswer((Answer<Void>) invocation -> {
            RuntimeEnvironment.application.startActivity(invocation.getArgument(0));
            return null;
        }).when(starter).startActivityForResult(any(Intent.class), anyInt());
        when(starter.getContext()).thenReturn(RuntimeEnvironment.application);
        when(helper.isSearching()).thenReturn(isSearching);
    }

    @Test
    public void populateUnreadNotSearching() {
        View view = inflate(R.layout.item_minerva_course);
        Course course = generate(Course.class);
        long unread = hasUnread ? 20 : 0;
        Pair<Course, Long> data = Pair.create(course, unread);
        MinervaCourseViewHolder viewHolder = new MinervaCourseViewHolder(view, listener, helper, starter);

        viewHolder.populate(data);

        assertTextIs(course.getTitle(), view.findViewById(R.id.name));
        assertNotEmpty(view.findViewById(R.id.subtitle));

        int expected = hasUnread ? View.VISIBLE : View.GONE;
        int actual = view.findViewById(R.id.unread_icon).getVisibility();
        assertEquals(expected, actual);

        // Test opening a course activity.
        view.performClick();
        Intent expectedIntent = new Intent(view.getContext(), CourseActivity.class);
        Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
        assertEquals(course, actualIntent.getParcelableExtra(CourseActivity.ARG_COURSE));
        assertEquals(CourseActivity.Tab.ANNOUNCEMENTS, actualIntent.getIntExtra(CourseActivity.ARG_TAB, -1));

        // Check if search handle is in the proper state.
        ImageView dragHandle = view.findViewById(R.id.drag_handle);
        expected = isSearching ? View.GONE : View.VISIBLE;
        assertEquals(expected, dragHandle.getVisibility());

        // Check if the handle is properly hidden/shown
        viewHolder.onSearchStateChange(!isSearching);
        expected = isSearching ? View.VISIBLE : View.GONE; // Inverted
        assertEquals(expected, dragHandle.getVisibility());

        // Assert drag handle events are properly dispatched.
        MotionEvent motionEvent = MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN,
                0f,
                0f,
                0
        );
        dragHandle.dispatchTouchEvent(motionEvent);

        int callCount = isSearching ? 0 : 1;
        verify(listener, times(callCount)).onStartDrag(any(RecyclerView.ViewHolder.class));
    }
}