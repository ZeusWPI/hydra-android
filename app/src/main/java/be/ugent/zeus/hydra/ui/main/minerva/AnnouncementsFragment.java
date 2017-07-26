package be.ugent.zeus.hydra.ui.main.minerva;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.database.minerva.AnnouncementDao;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.observers.AdapterObserver;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.repository.observers.SuccessObserver;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.recyclerview.EmptyViewObserver;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.MultiSelectDiffAdapter;
import org.threeten.bp.ZonedDateTime;

import java.util.Collection;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * Displays all unread announcements, with the newest first.
 *
 * @author Niko Strijbol
 */
public class AnnouncementsFragment extends LifecycleFragment implements MultiSelectDiffAdapter.Callback<Announcement> {

    private static final String TAG = "AnnouncementsFragment";

    private ResultStarter resultStarter;
    private ProgressBar progressBar;
    private AnnouncementsViewModel model;
    private RecyclerView recyclerView;
    private AnnouncementsAdapter adapter;
    private ActionMode actionMode;

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
        adapter.addCallback(this);

        recyclerView = $(view, R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new EmptyViewObserver(recyclerView, $(view, R.id.no_data_view)));

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
                // Stop the CAB - the adapter is cleared automatically.
                if (actionMode != null) {
                    actionMode.finish();
                }
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

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_main_minerva_announcements_cab, menu);
            int colorInt = ContextCompat.getColor(getContext(), R.color.white);
            BaseActivity.tintToolbarIcons(colorInt, menu, R.id.minerva_announcements_mark_done, R.id.action_select_all);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_select_all:
                    adapter.setAllChecked(true);
                    return true;
                case R.id.minerva_announcements_mark_done:
                    markSelectedAsRead();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // First we remove the callback to prevent circular callbacks (TODO: better method)
            adapter.removeCallback(AnnouncementsFragment.this);
            adapter.setAllChecked(false);
            actionMode = null;
            // Re-register the callback
            adapter.addCallback(AnnouncementsFragment.this);
        }
    };

    @Override
    public void onStateChanged(MultiSelectDiffAdapter<Announcement> adapter) {
        if (adapter.hasSelected()) {
            if (actionMode == null) {
                actionMode = getActivity().startActionMode(actionModeCallback);
                actionMode.setTitleOptionalHint(true);
            }
            actionMode.setTitle(getString(R.string.selected_n, adapter.selectedSize()));
        } else {
            actionMode.finish();
        }
    }

    private void markSelectedAsRead() {
        AnnouncementDao dao = new AnnouncementDao(getContext());
        Collection<Announcement> announcements = adapter.getSelectedItems();
        ZonedDateTime read = ZonedDateTime.now();
        for (Announcement an : announcements) {
            an.setRead(read);
        }
        dao.update(announcements);
        // Request a refresh of the data to update the list of announcements.
        model.requestRefresh(getContext());
        RefreshBroadcast.broadcastRefresh(getContext(), true);
        Toast.makeText(getContext().getApplicationContext(),
                getResources().getQuantityString(R.plurals.minerva_marked_announcements, announcements.size(), announcements.size()),
                Toast.LENGTH_SHORT)
                .show();
    }
}