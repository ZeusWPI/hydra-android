package be.ugent.zeus.hydra.ui.main;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.loaders.LoaderResult;
import be.ugent.zeus.hydra.ui.common.plugins.loader.LoaderCallback;
import be.ugent.zeus.hydra.data.database.minerva.AgendaDao;
import be.ugent.zeus.hydra.data.database.minerva.AnnouncementDao;
import be.ugent.zeus.hydra.data.auth.AccountUtils;
import be.ugent.zeus.hydra.data.auth.MinervaConfig;
import be.ugent.zeus.hydra.data.database.minerva.CourseDao;
import be.ugent.zeus.hydra.data.sync.course.Adapter;
import be.ugent.zeus.hydra.data.sync.MinervaAdapter;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.data.sync.SyncUtils;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.ui.common.plugins.ProgressBarPlugin;
import be.ugent.zeus.hydra.ui.common.plugins.RecyclerViewPlugin;
import be.ugent.zeus.hydra.ui.common.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.common.plugins.common.PluginFragment;

import java.io.IOException;
import java.util.List;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * Displays Minerva items.
 *
 * @author silox
 * @author Niko Strijbol
 */
public class MinervaFragment extends PluginFragment implements LoaderCallback<List<Course>> {

    private static final String TAG = "MinervaFragment";

    private static final String LOADER_ARG_SORT = "loaderSortMode";

    private View authWrapper;
    private Snackbar syncBar;

    private MinervaCourseAdapter adapter = new MinervaCourseAdapter();
    private AccountManager manager;
    private CourseDao courseDao;

    private RecyclerViewPlugin<Course> plugin =
            new RecyclerViewPlugin<>(this, adapter);
    //This will only be called if manually set to send broadcasts.
    private BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            assert getView() != null;
            switch (intent.getAction()) {
                case SyncBroadcast.SYNC_START:
                    Log.d(TAG, "Start!");
                    ensureSyncStatus(getString(R.string.minerva_sync_getting_courses));
                    return;
                case SyncBroadcast.SYNC_DONE:
                    Log.d(TAG, "Done!");
                    ensureSyncStatus(getString(R.string.minerva_sync_done));
                    syncBar.dismiss();
                    syncBar = null;
                    plugin.showRecyclerView();
                    plugin.restartLoader();
                    return;
                case SyncBroadcast.SYNC_ERROR:
                    Log.d(TAG, "Error");
                    ensureSyncStatus(getString(R.string.failure));
                    syncBar.setDuration(Snackbar.LENGTH_LONG);
                    return;
                case SyncBroadcast.SYNC_PROGRESS_WHATS_NEW:
                    Log.d(TAG, "Progress");
                    int current = intent.getIntExtra(SyncBroadcast.ARG_SYNC_PROGRESS_CURRENT, 0);
                    int total = intent.getIntExtra(SyncBroadcast.ARG_SYNC_PROGRESS_TOTAL, 0);
                    ensureSyncStatus(getString(R.string.minerva_sync_progress, current, total));
            }
        }
    };

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.enableProgress()
                .setAutoStart(false)
                .setSuccessCallback(this::receiveData);
        plugins.add(plugin);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_minerva, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        plugin.getProgressBarPlugin().ifPresent(ProgressBarPlugin::hideProgressBar);

        this.manager = AccountManager.get(getContext());
        this.courseDao = new CourseDao(getContext());

        Button authorize = $(view, R.id.authorize);
        authorize.setOnClickListener(v -> maybeLaunchAuthorization());

        authWrapper = $(view, R.id.auth_wrapper);

        plugin.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private boolean isLoggedIn() {
        return AccountUtils.hasAccount(getContext());
    }

    private void maybeLaunchAuthorization() {
        if (!isLoggedIn()) {
            manager.addAccount(MinervaConfig.ACCOUNT_TYPE, MinervaConfig.DEFAULT_SCOPE, null, null, getActivity(), accountManagerFuture -> {
                try {
                    Bundle result = accountManagerFuture.getResult();
                    Log.d(TAG, "Account " + result.getString(AccountManager.KEY_ACCOUNT_NAME) + " was created.");
                    onAccountAdded();
                } catch (OperationCanceledException e) {
                    Toast.makeText(getContext().getApplicationContext(), R.string.minerva_no_permission, Toast.LENGTH_LONG).show();
                } catch (IOException | AuthenticatorException e) {
                    Log.w(TAG, "Account not added.", e);
                }
            }, null);
        }
    }

    private void onAccountAdded() {
        //Get an account
        Account account = AccountUtils.getAccount(getContext());

        //Request first sync
        Log.d(TAG, "Requesting first sync...");
        Bundle bundle = new Bundle();
        bundle.putBoolean(MinervaAdapter.EXTRA_FIRST_SYNC, true);
        bundle.putBoolean(Adapter.EXTRA_SCHEDULE_AGENDA, true);
        bundle.putBoolean(Adapter.EXTRA_SCHEDULE_ANNOUNCEMENTS, true);
        SyncUtils.requestSync(account, MinervaConfig.COURSE_AUTHORITY, bundle);
    }

    /**
     * Thanks to the constructor the loaders are not started in the parent function. We start them here
     * manually.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        maybeLoadData();
    }

    /**
     * Check if we can load data and if yes, do it.
     */
    private void maybeLoadData() {
        //If we are logged in, we can start loading the data.
        if (isLoggedIn()) {
            authWrapper.setVisibility(View.GONE);
            plugin.getProgressBarPlugin().ifPresent(ProgressBarPlugin::showProgressBar);
            plugin.startLoader();
        }
    }

    private void receiveData(List<Course> data) {
        plugin.showRecyclerView();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isLoggedIn()) {
            inflater.inflate(R.menu.menu_minerva, menu);
            SearchView view = (SearchView) menu.findItem(R.id.action_search).getActionView();
            view.setOnQueryTextListener(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                signOut();
                return true;
            case R.id.action_sync_all:
                manualSync(true, true);
                return true;
            case R.id.action_sync_announcements:
                manualSync(true, false);
                return true;
            case R.id.action_sync_calendar:
                manualSync(false, true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void manualSync(boolean announcements, boolean calendar) {
        Account account = AccountUtils.getAccount(getContext());
        Bundle bundle = new Bundle();
        if (announcements) {
            bundle.putBoolean(Adapter.EXTRA_SCHEDULE_ANNOUNCEMENTS, true);
        }
        if (calendar) {
            bundle.putBoolean(Adapter.EXTRA_SCHEDULE_AGENDA, true);
        }
        Toast.makeText(getContext(), R.string.minerva_syncing, Toast.LENGTH_LONG)
                .show();
        SyncUtils.requestSync(account, MinervaConfig.COURSE_AUTHORITY, bundle);
    }

    /**
     * Sign out and hide the data.
     */
    private void signOut() {
        //Sign out first, and then remove all data.
        Account a = AccountUtils.getAccount(getContext());
        ContentResolver.cancelSync(a, MinervaConfig.COURSE_AUTHORITY);
        ContentResolver.cancelSync(a, MinervaConfig.ANNOUNCEMENT_AUTHORITY);
        ContentResolver.cancelSync(a, MinervaConfig.CALENDAR_AUTHORITY);
        Toast.makeText(getContext(), "Logging out...", Toast.LENGTH_SHORT).show();
        manager.removeAccount(a, accountManagerFuture -> {
            //Delete items
            adapter.clear();
            //Hide list
            plugin.hideRecyclerView();
            //Hide progress
            plugin.getProgressBarPlugin().ifPresent(ProgressBarPlugin::hideProgressBar);
            //Show login prompt
            authWrapper.setVisibility(View.VISIBLE);
            //Destroy loaders
            plugin.destroyLoader();
            //Delete database
            clearDatabase();
            //Reload options
            getActivity().invalidateOptionsMenu();
        }, null);
    }

    private void clearDatabase() {
        courseDao.deleteAll();
        AnnouncementDao announcementDao = new AnnouncementDao(getContext());
        announcementDao.deleteAll();
        AgendaDao dao = new AgendaDao(getContext());
        dao.deleteAll();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.registerReceiver(syncReceiver, SyncBroadcast.getBroadcastFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.unregisterReceiver(syncReceiver);
        if (syncBar != null && syncBar.isShown()) {
            syncBar.dismiss();
            syncBar = null;
        }
    }

    /**
     * Ensure the sync status is enabled, and set the the given text on the snackbar.
     *
     * @param text To display on the snackbar.
     */
    private void ensureSyncStatus(String text) {
        assert getView() != null;
        if (syncBar == null) {
            authWrapper.setVisibility(View.GONE);
            syncBar = Snackbar.make(getView(), text, Snackbar.LENGTH_INDEFINITE);
            syncBar.show();
        } else {
            syncBar.setText(text);
        }
    }

    @Override
    public Loader<LoaderResult<List<Course>>> getLoader(Bundle args) {
        return new MinervaCoursesLoader(getContext(), courseDao);
    }
}