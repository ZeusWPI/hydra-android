package be.ugent.zeus.hydra.ui.main.minerva;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.auth.AccountUtils;
import be.ugent.zeus.hydra.data.auth.MinervaConfig;
import be.ugent.zeus.hydra.data.database.minerva.AgendaDao;
import be.ugent.zeus.hydra.data.database.minerva.AnnouncementDao;
import be.ugent.zeus.hydra.data.database.minerva.CourseDao;
import be.ugent.zeus.hydra.data.sync.MinervaAdapter;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.data.sync.SyncUtils;
import be.ugent.zeus.hydra.data.sync.announcement.AnnouncementNotificationBuilder;
import be.ugent.zeus.hydra.data.sync.course.CourseAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;

/**
 * Overview fragment for Minerva in the main activity. Manages logging in, and manages showing the tabs.
 * Which tabs are displayed is managed by {@link MinervaPagerAdapter}.
 *
 * @author Niko Strijbol
 */
public class OverviewFragment extends Fragment implements ResultStarter {

    private static final String TAG = "OverviewFragment";
    private static final int REQUEST_ANNOUNCEMENT_CHANGED_CODE = 56532;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MinervaPagerAdapter minervaPagerAdapter;

    private View authWrapper;
    private AccountManager manager;
    private Snackbar syncBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_minerva_overview, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button authorize = view.findViewById(R.id.authorize);
        authorize.setOnClickListener(v -> maybeLaunchAuthorization());

        manager = AccountManager.get(getContext());
        authWrapper = view.findViewById(R.id.auth_wrapper);
        viewPager = view.findViewById(R.id.pager);
        tabLayout = getActivity().findViewById(R.id.tab_layout);

        // Get the viewModels of the tabs.
    }

    private void onLoggedIn() {
        authWrapper.setVisibility(View.GONE);
        tabLayout.setVisibility(View.VISIBLE);
        minervaPagerAdapter = new MinervaPagerAdapter(getChildFragmentManager());
        minervaPagerAdapter.setLoggedIn(true);
        viewPager.setAdapter(minervaPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //
            }

            @Override
            public void onPageSelected(int position) {
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setVisibility(View.VISIBLE);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //If we are logged in, we can start loading the data.
        if (isLoggedIn()) {
            onLoggedIn();
            tabLayout.setVisibility(View.VISIBLE);
        }
    }

    private void onAccountAdded() {
        //Get an account
        Account account = AccountUtils.getAccount(getContext());

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(getContext());
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN,  null);

        //Request first sync
        Log.d(TAG, "Requesting first sync...");
        Bundle bundle = new Bundle();
        bundle.putBoolean(MinervaAdapter.EXTRA_FIRST_SYNC, true);
        bundle.putBoolean(CourseAdapter.EXTRA_SCHEDULE_AGENDA, true);
        bundle.putBoolean(CourseAdapter.EXTRA_SCHEDULE_ANNOUNCEMENTS, true);
        SyncUtils.requestSync(account, MinervaConfig.COURSE_AUTHORITY, bundle);
        onLoggedIn();
    }

    /**
     * @return True if the user is logged in to minerva.
     */
    private boolean isLoggedIn() {
        return AccountUtils.hasAccount(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tabLayout.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isLoggedIn()) {
            inflater.inflate(R.menu.menu_main_minerva_overview, menu);
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
            bundle.putBoolean(CourseAdapter.EXTRA_SCHEDULE_ANNOUNCEMENTS, true);
        }
        if (calendar) {
            bundle.putBoolean(CourseAdapter.EXTRA_SCHEDULE_AGENDA, true);
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
            // Delete any notifications that could be present.
            AnnouncementNotificationBuilder.cancelAll(getContext());
            //Delete items
            if (minervaPagerAdapter != null) {
                minervaPagerAdapter.setLoggedIn(false);
            }
            //Hide fragments
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            //Show login prompt
            authWrapper.setVisibility(View.VISIBLE);
            //Destroy loaders
            //model.destroyInstance();
            //Delete database
            clearDatabase();
            //Reload options
            getActivity().invalidateOptionsMenu();
        }, null);
    }

    private void clearDatabase() {
        CourseDao courseDao = new CourseDao(getContext());
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
    public void onActivityResult(int requestCode, int resultCode,  @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the result to the nested fragments.
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public int getRequestCode() {
        return REQUEST_ANNOUNCEMENT_CHANGED_CODE;
    }
}