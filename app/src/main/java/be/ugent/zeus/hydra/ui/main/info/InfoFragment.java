package be.ugent.zeus.hydra.ui.main.info;

import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.repository.observers.AdapterObserver;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.ui.InfoSubItemActivity;

/**
 * Display info items.
 *
 * @author Niko Strijbol
 */
public class InfoFragment extends Fragment {

    private static final String TAG = "InfoFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InfoListAdapter adapter = new InfoListAdapter();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        ProgressBar progressBar = view.findViewById(R.id.progress_bar);

        Bundle bundle = getArguments();
        // If we receive a list as argument, just show that list. No need to load anything.
        if (bundle != null && bundle.getParcelableArrayList(InfoSubItemActivity.INFO_ITEMS) != null) {
            adapter.setItems(bundle.getParcelableArrayList(InfoSubItemActivity.INFO_ITEMS));
            progressBar.setVisibility(View.GONE);
        } else {
            InfoViewModel model = ViewModelProviders.of(this).get(InfoViewModel.class);
            model.getData().observe(this, ErrorObserver.with(this::onError));
            model.getData().observe(this, new ProgressObserver<>(progressBar));
            model.getData().observe(this, new AdapterObserver<>(adapter));
        }
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG).show();
    }
}