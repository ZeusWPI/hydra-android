package be.ugent.zeus.hydra.ui.main.minerva;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.observers.AdapterObserver;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.repository.observers.SuccessObserver;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.ui.common.recyclerview.ordering.DragCallback;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * Displays all unread announcements, with the newest first.
 *
 * @author Niko Strijbol
 */
public class AnnouncementsFragment extends LifecycleFragment {

    private static final String TAG = "AnnouncementsFragment";

    private ResultStarter resultStarter;
    private ProgressBar progressBar;
    private AnnouncementsViewModel model;
    private RecyclerView recyclerView;
    private AnnouncementsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_minerva_announcements, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ResultViewModel model = ViewModelProviders.of(getActivity()).get(ResultViewModel.class);
        resultStarter = model.getResultStarter();

        adapter = new AnnouncementsAdapter(resultStarter);

        recyclerView = $(view, R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        progressBar = $(view, R.id.progress_bar);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressBar.setVisibility(View.VISIBLE);
        model = ViewModelProviders.of(getActivity()).get(AnnouncementsViewModel.class);
        model.getData().observe(this, ErrorObserver.with(this::onError));
        model.getData().observe(this, new ProgressObserver<>(progressBar));
        model.getData().observe(this, new AdapterObserver<>(adapter));
        model.getData().observe(this, new SuccessObserver<List<Announcement>>() {
            @Override
            protected void onSuccess(List<Announcement> data) {
                recyclerView.setVisibility(View.VISIBLE);
                getActivity().invalidateOptionsMenu();
            }
        });
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getView(), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == resultStarter.getRequestCode() && resultCode == RESULT_OK) {
            RefreshBroadcast.broadcastRefresh(getContext(), true);
            model.requestRefresh(getContext());
        }
    }
}