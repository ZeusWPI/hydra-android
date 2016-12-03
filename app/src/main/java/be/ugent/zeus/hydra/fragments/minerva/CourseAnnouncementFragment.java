package be.ugent.zeus.hydra.fragments.minerva;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.minerva.AnnouncementActivity;
import be.ugent.zeus.hydra.loaders.DataCallback;
import be.ugent.zeus.hydra.loaders.LoaderProvider;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDaoLoader;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.plugins.LoaderPlugin;
import be.ugent.zeus.hydra.plugins.ProgressBarPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.recyclerview.adapters.common.EmptyItemLoader;
import be.ugent.zeus.hydra.recyclerview.adapters.minerva.AnnouncementAdapter;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;

import java.util.ArrayList;
import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class CourseAnnouncementFragment extends PluginFragment implements DataCallback<List<Announcement>>, LoaderProvider<List<Announcement>> {

    private static final String ARG_COURSE = "argCourse";
    private static final String TAG = "CourseAnnouncementFragm";

    private Course course;
    private AnnouncementDao dao;
    private AnnouncementAdapter unreadAdapter;
    private AnnouncementAdapter readAdapter;

    private final ProgressBarPlugin progressBarPlugin = new ProgressBarPlugin();
    private LoaderPlugin<List<Announcement>> plugin = new LoaderPlugin<>(this, this, progressBarPlugin);

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
        plugins.add(progressBarPlugin);
        plugins.add(plugin);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        course = getArguments().getParcelable(ARG_COURSE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_minerva_course_announcements, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dao = new AnnouncementDao(getContext());
        readAdapter = new AnnouncementAdapter(R.layout.item_no_data);
        unreadAdapter = new AnnouncementAdapter(R.layout.item_no_data, this);

        RecyclerView recyclerView = $(view, R.id.recycler_view);
        RecyclerFastScroller scroller = $(view, R.id.fast_scroller);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        RvJoiner joiner = new RvJoiner();
        joiner.add(new JoinableLayout(R.layout.fragment_minerva_announcement_unread_header));
        joiner.add(new JoinableAdapter(unreadAdapter, EmptyItemLoader.ITEMS_VIEW, EmptyItemLoader.EMPTY_VIEW));
        joiner.add(new JoinableLayout(R.layout.item_minerva_announcement_read_header));
        joiner.add(new JoinableAdapter(readAdapter, EmptyItemLoader.ITEMS_VIEW, EmptyItemLoader.EMPTY_VIEW));
        recyclerView.setAdapter(joiner.getAdapter());

        scroller.attachRecyclerView(recyclerView);
        //HydraActivity a = (HydraActivity) getActivity();
        //scroller.attachAppBarLayout(a.$(R.id.coordinator_layout), a.$(R.id.app_bar_layout));
    }

    /**
     * Receive the data if the request was completed successfully.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(@NonNull List<Announcement> data) {
        List<Announcement> unread = new ArrayList<>();
        List<Announcement> read = new ArrayList<>();

        //Split data
        for (Announcement a: data) {
            if(a.isRead()) {
                read.add(a);
            } else {
                unread.add(a);
            }
        }

        unreadAdapter.setItems(unread);
        readAdapter.setItems(read);
    }

    @Override
    public void receiveError(@NonNull Throwable e) {
        Log.e(TAG, "Error while receiving data: ", e);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == AnnouncementActivity.RESULT_ANNOUNCEMENT && resultCode == Activity.RESULT_OK) {
            int id = data.getIntExtra(AnnouncementActivity.RESULT_ARG_ANNOUNCEMENT_ID, 0);
            int pos = unreadAdapter.positionOf(id);
            Announcement a = unreadAdapter.get(pos);
            unreadAdapter.remove(pos);
            readAdapter.add(a);
            Snackbar.make(getView(), "Als gelezen gemarkeerd.", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<ThrowableEither<List<Announcement>>> getLoader(Context context) {
        return new AnnouncementDaoLoader(context, dao, course);
    }
}