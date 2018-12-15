package be.ugent.zeus.hydra.minerva.calendar.upcominglist;

import android.arch.lifecycle.ViewModelProviders;
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
import be.ugent.zeus.hydra.minerva.course.Course;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import static be.ugent.zeus.hydra.utils.FragmentUtils.requireArguments;
import static be.ugent.zeus.hydra.utils.FragmentUtils.requireView;

/**
 * Displays the upcoming calendar for a given course.
 *
 * @author Niko Strijbol
 */
public class UpcomingCalendarForCourseFragment extends Fragment {

    private static final String TAG = "UpcomingCalendarFrag";

    private static final String ARG_COURSE = "argCourse";

    public static UpcomingCalendarForCourseFragment newInstance(Course course) {
        UpcomingCalendarForCourseFragment fragment = new UpcomingCalendarForCourseFragment();
        Bundle data = new Bundle();
        data.putParcelable(ARG_COURSE, course);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_minerva_course_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AgendaAdapter adapter = new AgendaAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        Course course = requireArguments(this).getParcelable(ARG_COURSE);
        AgendaViewModel model = ViewModelProviders.of(this).get(AgendaViewModel.class);
        model.setCourse(course);
        model.getData().observe(this, PartialErrorObserver.with(this::onError));
        model.getData().observe(this, new AdapterObserver<>(adapter));
        model.getData().observe(this, new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(requireView(this), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .show();
    }
}