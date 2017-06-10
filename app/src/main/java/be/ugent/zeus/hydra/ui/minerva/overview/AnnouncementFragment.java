package be.ugent.zeus.hydra.ui.minerva.overview;

import android.app.NotificationManager;
import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.data.sync.announcement.AnnouncementNotificationBuilder;
import be.ugent.zeus.hydra.repository.observers.AdapterObserver;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * Show Minerva announcements.
 *
 * @author Niko Strijbol
 */
public class AnnouncementFragment extends LifecycleFragment {

    private static final String TAG = "AnnouncementFragment";
    private static final String ARG_COURSE = "argCourse";

    private Course course;

    public static AnnouncementFragment newInstance(Course course) {
        AnnouncementFragment fragment = new AnnouncementFragment();
        Bundle data = new Bundle();
        data.putParcelable(ARG_COURSE, course);
        fragment.setArguments(data);
        return fragment;
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

        AnnouncementAdapter adapter = new AnnouncementAdapter();
        RecyclerView recyclerView = $(view, R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        RecyclerFastScroller scroller = $(view, R.id.fast_scroller);
        scroller.attachRecyclerView(recyclerView);

        Course course = getArguments().getParcelable(ARG_COURSE);
        AnnouncementViewModel model = ViewModelProviders.of(this).get(AnnouncementViewModel.class);
        model.setCourse(course);
        model.getData().observe(this, ErrorObserver.with(this::onError));
        model.getData().observe(this, new AdapterObserver<>(adapter));
        model.getData().observe(this, new ProgressObserver<>($(view, R.id.progress_bar)));
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .show();
    }
}