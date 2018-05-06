package be.ugent.zeus.hydra.minerva.announcement.courselist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.AdapterObserver;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.minerva.course.Course;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import static android.app.Activity.RESULT_OK;
import static be.ugent.zeus.hydra.utils.FragmentUtils.requireArguments;
import static be.ugent.zeus.hydra.utils.FragmentUtils.requireView;

/**
 * Displays a list of announcements for a given course.
 *
 * <h1>Results</h1>
 * This fragment will call {@link android.app.Activity#setResult(int)} to indicate a refresh is needed if an
 * announcement is marked as read from this fragment, i.e. when the user clicks on an announcement in this fragment.
 *
 * When the above scenario occurs, {@link #RESULT_ANNOUNCEMENT_UPDATED} will be set to {@code true}.
 *
 * @author Niko Strijbol
 */
public class AnnouncementsForCourseFragment extends Fragment implements ResultStarter {

    public static final String RESULT_ANNOUNCEMENT_UPDATED = "be.ugent.zeus.hydra.result.minerva.course.announcement.read";
    private static final String TAG = "AnnForCourseFragment";
    private static final String ARG_COURSE = "argCourse";

    private static final int ANNOUNCEMENT_RESULT_CODE = 5555;

    private AnnouncementViewModel viewModel;

    @SuppressWarnings("TypeMayBeWeakened")
    public static AnnouncementsForCourseFragment newInstance(Course course) {
        AnnouncementsForCourseFragment fragment = new AnnouncementsForCourseFragment();
        Bundle data = new Bundle();
        data.putParcelable(ARG_COURSE, course);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_minerva_course_announcements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Adapter adapter = new Adapter(this);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        // TODO
        // adapter.registerAdapterDataObserver(new EmptyViewObserver(recyclerView, view.findViewById(R.id.no_data_view)));

        RecyclerFastScroller scroller = view.findViewById(R.id.fast_scroller);
        scroller.attachRecyclerView(recyclerView);

        Bundle arguments = requireArguments(this);
        Course course = arguments.getParcelable(ARG_COURSE);
        viewModel = ViewModelProviders.of(this).get(AnnouncementViewModel.class);
        viewModel.setCourse(course);
        viewModel.getData().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new AdapterObserver<>(adapter));
        viewModel.getData().observe(this, new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(requireView(this), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ANNOUNCEMENT_RESULT_CODE && resultCode == RESULT_OK) {
            // One of the announcements was marked as read, so update the UI.
            viewModel.requestRefresh();
            Intent intent = new Intent();
            intent.putExtra(RESULT_ANNOUNCEMENT_UPDATED, true);
            requireActivity().setResult(RESULT_OK, intent);
        }
    }

    @Override
    public int getRequestCode() {
        return ANNOUNCEMENT_RESULT_CODE;
    }
}