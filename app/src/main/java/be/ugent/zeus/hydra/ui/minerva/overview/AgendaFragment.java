package be.ugent.zeus.hydra.ui.minerva.overview;

import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
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
import be.ugent.zeus.hydra.repository.observers.AdapterObserver;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.ui.common.recyclerview.EmptyViewObserver;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

/**
 * Displays the agenda for a certain course.
 *
 * @author Niko Strijbol
 */
public class AgendaFragment extends Fragment {

    private static final String TAG = "AgendaFragment";

    private static final String ARG_COURSE = "argCourse";

    public static AgendaFragment newInstance(Course course) {
        AgendaFragment fragment = new AgendaFragment();
        Bundle data = new Bundle();
        data.putParcelable(ARG_COURSE, course);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_minerva_course_calendar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AgendaAdapter adapter = new AgendaAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new EmptyViewObserver(recyclerView, view.findViewById(R.id.no_data_view)));

        Course course = getArguments().getParcelable(ARG_COURSE);
        AgendaViewModel model = ViewModelProviders.of(this).get(AgendaViewModel.class);
        model.setCourse(course);
        model.getData().observe(this, ErrorObserver.with(this::onError));
        model.getData().observe(this, new AdapterObserver<>(adapter));
        model.getData().observe(this, new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .show();
    }
}