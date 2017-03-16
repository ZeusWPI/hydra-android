package be.ugent.zeus.hydra.fragments.minerva;

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
import be.ugent.zeus.hydra.loaders.plugin.LoaderProvider;
import be.ugent.zeus.hydra.loaders.LoaderResult;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDaoLoader;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementNotificationBuilder;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.recyclerview.adapters.minerva.AnnouncementAdapter;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class CourseAnnouncementFragment extends PluginFragment implements LoaderProvider<List<Announcement>> {

    private static final String ARG_COURSE = "argCourse";

    private Course course;
    private AnnouncementDao dao;
    private AnnouncementAdapter adapter = new AnnouncementAdapter();

    private RecyclerViewPlugin<Announcement, List<Announcement>> plugin = new RecyclerViewPlugin<>(this, adapter);

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
        plugin.hasProgress();
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
    public Loader<LoaderResult<List<Announcement>>> getLoader(int id, Bundle args) {
        return new AnnouncementDaoLoader(getContext(), dao, course);
    }
}