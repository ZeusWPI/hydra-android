package be.ugent.zeus.hydra.minerva.announcement.courselist;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.minerva.announcement.Announcement;
import be.ugent.zeus.hydra.minerva.announcement.SingleAnnouncementActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertNotEmpty;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class ViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.item_minerva_announcement);
        ResultStarter starter = mock(ResultStarter.class);
        doAnswer((Answer<Void>) invocation -> {
            RuntimeEnvironment.application.startActivity(invocation.getArgument(0));
            return null;
        }).when(starter).startActivityForResult(any(Intent.class), anyInt());
        when(starter.getContext()).thenReturn(RuntimeEnvironment.application);
        Announcement announcement = generate(Announcement.class);
        ViewHolder viewHolder = new ViewHolder(view, starter);
        viewHolder.populate(announcement);

        assertTextIs(announcement.getTitle(), view.findViewById(R.id.title));
        assertNotEmpty(view.findViewById(R.id.subtitle));

        @ColorInt
        int expected = announcement.isRead() ? Color.TRANSPARENT : Color.WHITE;
        ColorDrawable drawable = (ColorDrawable) view.getBackground();
        assertEquals(expected, drawable.getColor());

        view.findViewById(R.id.clickable_view).performClick();

        Intent expectedIntent = new Intent(view.getContext(), SingleAnnouncementActivity.class);
        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();

        assertEquals(expectedIntent.getComponent(), actual.getComponent());
        assertEquals(announcement, actual.getParcelableExtra(SingleAnnouncementActivity.ARG_ANNOUNCEMENT));
    }
}