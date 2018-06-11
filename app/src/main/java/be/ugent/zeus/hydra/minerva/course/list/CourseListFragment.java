package be.ugent.zeus.hydra.minerva.course.list;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import android.widget.ProgressBar;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.AdapterObserver;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.database.RepositoryFactory;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.common.ui.recyclerview.ordering.DragCallback;
import be.ugent.zeus.hydra.common.ui.recyclerview.ordering.OnStartDragListener;
import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.course.CourseRepository;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static be.ugent.zeus.hydra.utils.FragmentUtils.requireView;

/**
 * Display a list of courses.
 *
 * The parent fragment or activity of this fragment must implement {@link ResultStarter}. First the parent
 * fragment is tested. If it is an instance of {@link ResultStarter}, it will be used.
 *
 * @author silox
 * @author Niko Strijbol
 */
public class CourseListFragment extends Fragment implements OnStartDragListener {

    private static final String TAG = "CourseListFragment";

    private MinervaCourseAdapter adapter;
    private ItemTouchHelper helper;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private MinervaViewModel model;
    private ResultStarter resultStarter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_minerva_courses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getParentFragment() instanceof ResultStarter) {
            resultStarter = (ResultStarter) getParentFragment();
        } else {
            resultStarter = (ResultStarter) getActivity();
        }

        CourseRepository courseDao = RepositoryFactory.getCourseRepository(getContext());
        adapter = new MinervaCourseAdapter(this, resultStarter);
        adapter.setCourseDao(courseDao);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        ItemTouchHelper.Callback callback = new DragCallback(adapter);
        helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(requireView(this), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressBar.setVisibility(View.VISIBLE);
        model = ViewModelProviders.of(requireActivity()).get(MinervaViewModel.class);
        model.getData().observe(this, PartialErrorObserver.with(this::onError));
        model.getData().observe(this, new ProgressObserver<>(progressBar));
        model.getData().observe(this, new AdapterObserver<>(adapter));
        model.getData().observe(this, new SuccessObserver<List<Pair<Course, Long>>>() {
            @Override
            protected void onSuccess(@NonNull List<Pair<Course, Long>> data) {
                recyclerView.setVisibility(View.VISIBLE);
                requireActivity().invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_minerva_courses, menu);
        SearchView view = (SearchView) menu.findItem(R.id.action_search).getActionView();
        view.setOnQueryTextListener(adapter);
        view.setOnCloseListener(adapter);
        view.setOnSearchClickListener(v -> adapter.onOpen());
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        helper.startDrag(viewHolder);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == resultStarter.getRequestCode() && resultCode == RESULT_OK) {
            model.requestRefresh();
        }
    }
}