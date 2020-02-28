package be.ugent.zeus.hydra.schamper;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.AdapterObserver;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.customtabs.CustomTabsHelper;
import be.ugent.zeus.hydra.common.ui.recyclerview.SpanItemSpacingDecoration;
import be.ugent.zeus.hydra.common.utils.ColourUtils;
import com.google.android.material.snackbar.Snackbar;

import static be.ugent.zeus.hydra.common.utils.FragmentUtils.requireBaseActivity;

/**
 * Display Schamper articles in the main activity.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SchamperFragment extends Fragment {

    private static final String TAG = "SchamperFragment";

    private ActivityHelper helper;
    private SchamperViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schamper, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = CustomTabsHelper.initHelper(getActivity(), null);
        helper.setShareMenu();
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(requireContext()));
        SchamperListAdapter adapter = new SchamperListAdapter(helper);
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(ColourUtils.resolveColour(requireContext(), R.attr.colorSecondary));

        viewModel = new ViewModelProvider(this).get(SchamperViewModel.class);
        viewModel.getData().observe(getViewLifecycleOwner(), PartialErrorObserver.with(this::onError));
        viewModel.getData().observe(getViewLifecycleOwner(), new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
        viewModel.getData().observe(getViewLifecycleOwner(), new AdapterObserver<>(adapter));
        viewModel.getRefreshing().observe(getViewLifecycleOwner(), swipeRefreshLayout::setRefreshing);
        swipeRefreshLayout.setOnRefreshListener(viewModel);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_refresh, menu);
        BaseActivity activity = requireBaseActivity(this);
        BaseActivity.tintToolbarIcons(activity.requireToolbar(), menu, R.id.action_refresh);
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

    @Override
    public void onStart() {
        super.onStart();
        helper.bindCustomTabsService(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        helper.unbindCustomTabsService(getActivity());
    }
}
