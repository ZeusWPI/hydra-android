package be.ugent.zeus.hydra.minerva.mainui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.analytics.Analytics;
import be.ugent.zeus.hydra.common.database.RepositoryFactory;
import be.ugent.zeus.hydra.common.sync.SyncUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.minerva.account.AccountUtils;
import be.ugent.zeus.hydra.minerva.account.MinervaConfig;
import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementRepository;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItemRepository;
import be.ugent.zeus.hydra.minerva.common.sync.MinervaAdapter;
import be.ugent.zeus.hydra.minerva.common.sync.NotificationHelper;
import be.ugent.zeus.hydra.minerva.common.sync.SyncBroadcast;
import be.ugent.zeus.hydra.minerva.course.CourseRepository;
import be.ugent.zeus.hydra.minerva.provider.DocumentContract;

import static be.ugent.zeus.hydra.utils.FragmentUtils.requireBaseActivity;

/**
 * Overview fragment for Minerva in the main activity. Manages logging in, and manages showing the tabs.
 * Which tabs are displayed is managed by {@link MinervaPagerAdapter}.
 *
 * @author Niko Strijbol
 */
public class OverviewFragment extends Fragment implements ResultStarter, MainActivity.ScheduledRemovalListener {

    private static final String TAG = "OverviewFragment";
    private static final int REQUEST_ANNOUNCEMENT_CHANGED_CODE = 56532;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MinervaPagerAdapter minervaPagerAdapter;

    private View authWrapper;
    private AccountManager manager;
    private Snackbar syncBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_minerva_overview, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button authorize = view.findViewById(R.id.authorize);
        authorize.setOnClickListener(v -> maybeLaunchAuthorization());

        manager = AccountManager.get(getContext());
        authWrapper = view.findViewById(R.id.auth_wrapper);
        viewPager = view.findViewById(R.id.pager);
        tabLayout = requireActivity().findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
    }

    private void onLoggedIn() {
        authWrapper.setVisibility(View.GONE);
        tabLayout.setVisibility(View.VISIBLE);
        minervaPagerAdapter = new MinervaPagerAdapter(getChildFragmentManager(), requireContext());
        minervaPagerAdapter.setLoggedIn(true);
        viewPager.setAdapter(minervaPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //
            }

            @Override
            public void onPageSelected(int position) {
                requireActivity().invalidateOptionsMenu();
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
                    Toast.makeText(requireContext(), R.string.minerva_login_no_permission, Toast.LENGTH_LONG).show();
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

        Analytics.getTracker(requireContext())
                .log(new LoginEvent());

        //Request first sync
        Log.d(TAG, "Requesting first sync...");
        Bundle bundle = new Bundle();
        bundle.putBoolean(MinervaAdapter.EXTRA_FIRST_SYNC, true);
        SyncUtils.requestSync(account, MinervaConfig.SYNC_AUTHORITY, bundle);
        onLoggedIn();
    }

    /**
     * @return True if the user is logged in to minerva.
     */
    private boolean isLoggedIn() {
        return AccountUtils.hasAccount(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (isLoggedIn()) {
            inflater.inflate(R.menu.menu_main_minerva_overview, menu);
            requireBaseActivity(this).tintToolbarIcons(menu, R.id.minerva_documents_link);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !hasDocumentsProvider(requireContext())) {
                menu.findItem(R.id.minerva_documents_link).setVisible(false).setEnabled(false);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static boolean hasDocumentsProvider(Context context) {
        final Intent intent = new Intent(android.provider.DocumentsContract.PROVIDER_INTERFACE);
        final List<ResolveInfo> infos = context.getPackageManager().queryIntentContentProviders(intent, 0);
        for (ResolveInfo info : infos) {
            if (DocumentContract.AUTHORITY.equals(info.providerInfo.authority)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                signOut();
                return true;
            case R.id.action_sync_all:
                manualSync();
                return true;
            case R.id.minerva_documents_link:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(DocumentsContract.buildRootUri(DocumentContract.AUTHORITY, DocumentContract.ROOT_ID));
                    if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(requireContext(), R.string.minerva_course_no_document_viewer, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "No document viewer found, aborting.");
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void manualSync() {
        Account account = AccountUtils.getAccount(getContext());
        Bundle bundle = new Bundle();
        Toast.makeText(getContext(), R.string.minerva_sync_started, Toast.LENGTH_LONG)
                .show();
        SyncUtils.requestSync(account, MinervaConfig.SYNC_AUTHORITY, bundle);
    }

    /**
     * Sign out and hide the data.
     */
    private void signOut() {
        //Sign out first, and then remove all data.
        Account a = AccountUtils.getAccount(getContext());
        ContentResolver.cancelSync(a, MinervaConfig.SYNC_AUTHORITY);
        Toast.makeText(getContext(), "Logging out...", Toast.LENGTH_SHORT).show();
        manager.removeAccount(a, accountManagerFuture -> {
            // Delete any notifications that could be present.
            NotificationHelper.cancelAll(requireContext());
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
            requireActivity().invalidateOptionsMenu();
        }, null);
    }

    private void clearDatabase() {
        AnnouncementRepository announcementDao = RepositoryFactory.getAnnouncementRepository(getContext());
        announcementDao.deleteAll();
        AgendaItemRepository agendaItemRepository = RepositoryFactory.getAgendaItemRepository(getContext());
        agendaItemRepository.deleteAll();
        CourseRepository courseDao = RepositoryFactory.getCourseRepository(getContext());
        courseDao.deleteAll();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(requireContext());
        manager.registerReceiver(syncReceiver, SyncBroadcast.getBroadcastFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(requireContext());
        manager.unregisterReceiver(syncReceiver);
        if (syncBar != null && syncBar.isShown()) {
            syncBar.dismiss();
            syncBar = null;
        }
    }

    //This will only be called if manually set to send broadcasts.
    private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            assert getView() != null;
            if (intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {
                case SyncBroadcast.SYNC_COURSES:
                    Log.d(TAG, "Courses!");
                    ensureSyncStatus(getString(R.string.minerva_sync_getting_courses));
                    return;
                case SyncBroadcast.SYNC_AGENDA:
                    Log.d(TAG, "Calendar!");
                    ensureSyncStatus(getString(R.string.minerva_sync_getting_calendar));
                    return;
                case SyncBroadcast.SYNC_CANCELLED:
                    Log.d(TAG, "Cancelled!");
                    ensureSyncStatus(getString(R.string.minerva_sync_cancelled), Snackbar.LENGTH_LONG);
                    return;
                case SyncBroadcast.SYNC_DONE:
                    Log.d(TAG, "Done!");
                    ensureSyncStatus(getString(R.string.minerva_sync_done), Snackbar.LENGTH_SHORT);
                    return;
                case SyncBroadcast.SYNC_ERROR:
                    Log.d(TAG, "Error");
                    ensureSyncStatus(getString(R.string.error_network), Snackbar.LENGTH_LONG);
                    return;
                case SyncBroadcast.SYNC_PROGRESS_WHATS_NEW:
                    int current = intent.getIntExtra(SyncBroadcast.ARG_SYNC_PROGRESS_CURRENT, 0);
                    int total = intent.getIntExtra(SyncBroadcast.ARG_SYNC_PROGRESS_TOTAL, 0);
                    Log.d(TAG, "Progress: handled " + current + " of " + total);
                    ensureSyncStatus(getString(R.string.minerva_sync_progress, current, total));
            }
        }
    };

    /**
     * Ensure the sync status is enabled, and set the the given text on the snackbar.
     *
     * @param text To display on the snackbar.
     */
    private void ensureSyncStatus(String text, int duration) {
        assert getView() != null;
        if (syncBar == null) {
            authWrapper.setVisibility(View.GONE);
            syncBar = Snackbar.make(getView(), text, Snackbar.LENGTH_INDEFINITE);
            syncBar.show();
        } else {
            syncBar.setText(text);
        }
        syncBar.setDuration(duration);
        syncBar.show();
    }

    /**
     * Ensure the sync status is enabled, and set the the given text on the snackbar.
     *
     * @param text To display on the snackbar.
     */
    private void ensureSyncStatus(String text) {
        ensureSyncStatus(text, Snackbar.LENGTH_INDEFINITE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

    @Override
    public void onRemovalScheduled() {
        // Propagate this to the children.
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof MainActivity.ScheduledRemovalListener) {
                ((MainActivity.ScheduledRemovalListener) fragment).onRemovalScheduled();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tabLayout.setVisibility(View.GONE);
    }
}