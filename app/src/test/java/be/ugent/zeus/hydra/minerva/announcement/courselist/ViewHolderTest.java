package be.ugent.zeus.hydra.minerva.announcement.courselist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.minerva.announcement.Announcement;
import be.ugent.zeus.hydra.minerva.announcement.SingleAnnouncementActivity;
import be.ugent.zeus.hydra.testing.RobolectricUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class ViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.item_minerva_announcement);
        ResultStarter starter = mock(ResultStarter.class);
        Context context = RobolectricUtils.getActivityContext();
        doAnswer((Answer<Void>) invocation -> {
            context.startActivity(invocation.getArgument(0));
            return null;
        }).when(starter).startActivityForResult(any(Intent.class), anyInt());
        when(starter.getContext()).thenReturn(context);
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
        Intent actual = getShadowApplication().getNextStartedActivity();

        assertEquals(expectedIntent.getComponent(), actual.getComponent());
        assertEquals(announcement, actual.getParcelableExtra(SingleAnnouncementActivity.ARG_ANNOUNCEMENT));
    }
}