package be.ugent.zeus.hydra.sko.lineup;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;

import java.util.List;
import java.util.Map;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import java9.util.stream.Collectors;
import java9.util.stream.Stream;
import java9.util.stream.StreamSupport;

import static be.ugent.zeus.hydra.utils.FragmentUtils.requireBaseActivity;
import static be.ugent.zeus.hydra.utils.FragmentUtils.requireView;

/**
 * Show the lineup.
 *
 * @author Niko Strijbol
 */
public class LineupFragment extends Fragment {

    private static final String TAG = "LineupFragment";

    private final LineupAdapter adapter = new LineupAdapter();
    private LineupViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sko_lineup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.sko_secondary_colour);

        recyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(LineupViewModel.class);
        viewModel.getData().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
        viewModel.getData().observe(this, SuccessObserver.with(this::receiveData));
        viewModel.getRefreshing().observe(this, refreshLayout::setRefreshing);
        refreshLayout.setOnRefreshListener(viewModel);
    }

    private void receiveData(@NonNull List<Artist> data) {

        // Sort the artists into stages.
        Map<String, List<Artist>> stages = StreamSupport.stream(data)
                .collect(Collectors.groupingBy(Artist::getStage));

        // Merge the sorted stages back into one flat list while prepending the stage as a title for each section.
        // This might be faster with a traditional loop, but the streams are fancier.
        List<ArtistOrTitle> masterList = StreamSupport.stream(stages.entrySet())
                .flatMap(e ->
                        Stream.concat(
                                Stream.of(new ArtistOrTitle(e.getKey())),
                                StreamSupport.stream(e.getValue()).map(ArtistOrTitle::new)
                        ))
                .collect(Collectors.toList());

        adapter.submitData(masterList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
        BaseActivity.tintToolbarIcons(requireBaseActivity(this).requireToolbar(), menu, R.id.action_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            viewModel.onRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(requireView(this), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> viewModel.onRefresh())
                .show();
    }
}