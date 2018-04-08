package be.ugent.zeus.hydra.minerva.announcement.unreadlist;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectAdapter;
import be.ugent.zeus.hydra.minerva.announcement.Announcement;
import be.ugent.zeus.hydra.minerva.announcement.SingleAnnouncementActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.*;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class AnnouncementsViewHolderTest {

    @Test
    public void populateChecked() {
        testGeneric(true);
    }

    @Test
    public void populateUnChecked() {
        testGeneric(false);
    }

    private void testGeneric(boolean checked) {
        ResultStarter starter = mock(ResultStarter.class);
        doAnswer((Answer<Void>) invocation -> {
            RuntimeEnvironment.application.startActivity(invocation.getArgument(0));
            return null;
        }).when(starter).startActivityForResult(any(Intent.class), anyInt());
        when(starter.getContext()).thenReturn(RuntimeEnvironment.application);
        View view = inflate(R.layout.item_minerva_extended_announcement);
        MultiSelectAdapter<Announcement> adapter = mock(MultiSelectAdapter.class);
        when(adapter.isChecked(anyInt())).thenReturn(checked);
        AnnouncementsViewHolder viewHolder = new AnnouncementsViewHolder(view, starter, adapter);

        Announcement announcement = generate(Announcement.class);
        viewHolder.populate(announcement);

        int expected = checked ? Color.WHITE : Color.TRANSPARENT;
        ColorDrawable background = (ColorDrawable) view.findViewById(R.id.background_container).getBackground();
        assertEquals(expected, background.getColor());

        assertTextIs(announcement.getTitle(), view.findViewById(R.id.title));
        assertNotEmpty(view.findViewById(R.id.subtitle));

        // Check normal click.
        when(adapter.hasSelected()).thenReturn(false);
        view.performClick();

        Intent expectedIntent = new Intent(view.getContext(), SingleAnnouncementActivity.class);
        Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
        assertEquals(announcement, actualIntent.getParcelableExtra(SingleAnnouncementActivity.ARG_ANNOUNCEMENT));

        // Check toggle if selection is active.
        when(adapter.hasSelected()).thenReturn(true);
        ArgumentCaptor<Integer> positionCaptor = ArgumentCaptor.forClass(Integer.class);
        view.performClick();

        verify(adapter).setChecked(positionCaptor.capture());
        assertEquals(RecyclerView.NO_POSITION, (int) positionCaptor.getValue());
    }
}