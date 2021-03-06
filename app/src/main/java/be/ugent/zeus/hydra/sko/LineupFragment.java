package be.ugent.zeus.hydra.sko;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import com.google.android.material.snackbar.Snackbar;

import static be.ugent.zeus.hydra.common.utils.FragmentUtils.requireBaseActivity;

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

        viewModel = new ViewModelProvider(this).get(LineupViewModel.class);
        viewModel.getData().observe(getViewLifecycleOwner(), PartialErrorObserver.with(this::onError));
        viewModel.getData().observe(getViewLifecycleOwner(), new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
        viewModel.getData().observe(getViewLifecycleOwner(), SuccessObserver.with(this::receiveData));
        viewModel.getRefreshing().observe(getViewLifecycleOwner(), refreshLayout::setRefreshing);
        refreshLayout.setOnRefreshListener(viewModel);
    }

    private void receiveData(@NonNull List<Artist> data) {

        // Sort the artists into stages.
        Map<String, List<Artist>> stages = data.stream()
                .collect(Collectors.groupingBy(Artist::getStage));

        // Merge the sorted stages back into one flat list while prepending the stage as a title for each section.
        // This might be faster with a traditional loop, but streams are fancier.
        List<ArtistOrTitle> masterList = stages.entrySet().stream()
                .flatMap(e ->
                        Stream.concat(
                                Stream.of(new ArtistOrTitle(e.getKey())),
                                e.getValue().stream().map(ArtistOrTitle::new)
                        ))
                .collect(Collectors.toList());

        adapter.submitData(masterList);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
        requireBaseActivity(this).tintToolbarIcons(menu, R.id.action_refresh);
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
        Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> viewModel.onRefresh())
                .show();
    }
}
