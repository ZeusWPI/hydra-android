package be.ugent.zeus.hydra.resto.extrafood;

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
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;

import static be.ugent.zeus.hydra.utils.FragmentUtils.requireArguments;
import static be.ugent.zeus.hydra.utils.FragmentUtils.requireView;

/**
 * @author Niko Strijbol
 */
public class FoodFragment extends Fragment {

    private static final String TAG = "FoodFragment";
    private static final String ARG_POSITION = "arg_position";

    private final FoodAdapter adapter = new FoodAdapter();
    private ExtraFoodViewModel viewModel;

    @SuppressWarnings("WeakerAccess")
    public static FoodFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        FoodFragment fragment = new FoodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resto_extra, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        int position = requireArguments(this).getInt(ARG_POSITION);

        viewModel = ViewModelProviders.of(requireActivity()).get(ExtraFoodViewModel.class);
        viewModel.getData().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
        viewModel.getData().observe(this, new SuccessObserver<ExtraFood>() {
            @Override
            protected void onSuccess(@NonNull ExtraFood data) {
                adapter.submitData(ExtraFoodViewModel.getFor(position, data));
            }

            @Override
            protected void onEmpty() {
                adapter.clear();
            }
        });
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(requireView(this), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> viewModel.onRefresh())
                .show();
    }
}