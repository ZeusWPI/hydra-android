package be.ugent.zeus.hydra.minerva.announcement.unreadlist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.database.RepositoryFactory;
import be.ugent.zeus.hydra.minerva.announcement.courselist.AnnouncementsForCourseFragment;
import be.ugent.zeus.hydra.minerva.sync.NotificationHelper;
import be.ugent.zeus.hydra.minerva.announcement.Announcement;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementRepository;
import be.ugent.zeus.hydra.common.arch.observers.AdapterObserver;
import be.ugent.zeus.hydra.common.arch.observers.ErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.recyclerview.EmptyViewObserver;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.MultiSelectDiffAdapter;
import be.ugent.zeus.hydra.main.ScheduledRemovalListener;
import be.ugent.zeus.hydra.minerva.announcement.SingleAnnouncementActivity;
import org.threeten.bp.Instant;

import java.util.Collection;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Displays all unread announcements, with the newest first.
 *
 * The parent fragment or activity of this fragment must implement {@link ResultStarter}. First the parent
 * fragment is tested. If it is an instance of {@link ResultStarter}, it will be used.
 *
 * @author Niko Strijbol
 */
public class UnreadAnnouncementsFragment extends Fragment implements MultiSelectDiffAdapter.Callback<Announcement>, ScheduledRemovalListener {

    private static final String TAG = "UnreadAnnouncementsFragment";

    private ResultStarter resultStarter;
    private ProgressBar progressBar;
    private AnnouncementsViewModel model;
    private RecyclerView recyclerView;
    private AnnouncementsAdapter adapter;
    private ActionMode actionMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_minerva_announcements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getParentFragment() instanceof ResultStarter) {
            resultStarter = (ResultStarter) getParentFragment();
        } else {
            resultStarter = (ResultStarter) getActivity();
        }

        adapter = new AnnouncementsAdapter(resultStarter);
        adapter.addCallback(this);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new EmptyViewObserver(recyclerView, view.findViewById(R.id.no_data_view)));

        progressBar = view.findViewById(R.id.progress_bar);
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
            model.requestRefresh();
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
            adapter.removeCallback(UnreadAnnouncementsFragment.this);
            adapter.setAllChecked(false);
            actionMode = null;
            // Re-register the callback
            adapter.addCallback(UnreadAnnouncementsFragment.this);
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
        // TODO: do this on a background thread
        AnnouncementRepository dao = RepositoryFactory.getAnnouncementRepository(getContext());
        Collection<Announcement> announcements = adapter.getSelectedItems();
        Instant read = Instant.now();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        for (Announcement an : announcements) {
            an.setRead(read);
            NotificationHelper.cancel(an, notificationManager);
        }
        dao.update(announcements);
        // Request a refresh of the data to update the list of announcements.
        Toast.makeText(getContext().getApplicationContext(),
                getResources().getQuantityString(R.plurals.minerva_marked_announcements, announcements.size(), announcements.size()),
                Toast.LENGTH_SHORT)
                .show();

        // Manually call set result. This should trigger an update.
        // TODO: find if there is a better way.
        Intent data = new Intent();
        data.putExtra(AnnouncementsForCourseFragment.RESULT_ANNOUNCEMENT_UPDATED, true);
        data.putExtra(SingleAnnouncementActivity.RESULT_ANNOUNCEMENT_READ, true);
        resultStarter.onActivityResult(resultStarter.getRequestCode(), RESULT_OK, data);
    }

    @Override
    public void onRemovalScheduled() {
        // Close the action mode if the user navigates to another tab.
        if (actionMode != null) {
            actionMode.finish();
        }
    }
}