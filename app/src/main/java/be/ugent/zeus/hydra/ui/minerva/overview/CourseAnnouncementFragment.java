package be.ugent.zeus.hydra.ui.minerva.overview;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.plugins.loader.LoaderCallback;
import be.ugent.zeus.hydra.ui.common.loaders.LoaderResult;
import be.ugent.zeus.hydra.data.database.minerva.AnnouncementDao;
import be.ugent.zeus.hydra.ui.minerva.AnnouncementsLoader;
import be.ugent.zeus.hydra.data.sync.announcement.AnnouncementNotificationBuilder;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.ui.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.ui.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.plugins.common.PluginFragment;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Show Minerva announcements.
 *
 * @author Niko Strijbol
 */
public class CourseAnnouncementFragment extends PluginFragment implements LoaderCallback<List<Announcement>> {

    private static final String ARG_COURSE = "argCourse";

    private Course course;
    private AnnouncementDao dao;
    private AnnouncementAdapter adapter = new AnnouncementAdapter();

    private RecyclerViewPlugin<Announcement> plugin = new RecyclerViewPlugin<>(this, adapter);

    public static CourseAnnouncementFragment newInstance(Course course) {
        CourseAnnouncementFragment fragment = new CourseAnnouncementFragment();
        Bundle data = new Bundle();
        data.putParcelable(ARG_COURSE, course);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.enableProgress();
        plugins.add(plugin);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        course = getArguments().getParcelable(ARG_COURSE);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check for notification we want to remove.
        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(course.getId(), AnnouncementNotificationBuilder.NOTIFICATION_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_minerva_course_announcements, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dao = new AnnouncementDao(getContext());
        plugin.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        RecyclerFastScroller scroller = $(view, R.id.fast_scroller);
        scroller.attachRecyclerView(plugin.getRecyclerView());
    }

    @Override
    public Loader<LoaderResult<List<Announcement>>> getLoader(Bundle args) {
        return new AnnouncementsLoader(getContext(), dao, course);
    }
}