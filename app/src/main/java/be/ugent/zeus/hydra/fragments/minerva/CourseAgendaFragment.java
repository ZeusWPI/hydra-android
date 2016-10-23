package be.ugent.zeus.hydra.fragments.minerva;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.minerva.agenda.AgendaDao;
import be.ugent.zeus.hydra.minerva.agenda.AgendaDaoLoader;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.recyclerview.adapters.minerva.AgendaAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class CourseAgendaFragment extends LoaderFragment<List<AgendaItem>> {

    private static final String ARG_COURSE = "argCourse";

    private Course course;
    private AgendaDao dao;
    private AgendaAdapter adapter;

    public static CourseAgendaFragment newInstance(Course course) {
        CourseAgendaFragment fragment = new CourseAgendaFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_minerva_course_announcements, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dao = new AgendaDao(getContext());
        adapter = new AgendaAdapter();

        RecyclerView recyclerView = $(view, R.id.recycler_view);
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Receive the data if the request was completed successfully.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(@NonNull List<AgendaItem> data) {
        adapter.setItems(data);
        hideProgressBar();
    }

    @Override
    public Loader<ThrowableEither<List<AgendaItem>>> getLoader() {
        return new AgendaDaoLoader(getContext(), dao, course);
    }
}