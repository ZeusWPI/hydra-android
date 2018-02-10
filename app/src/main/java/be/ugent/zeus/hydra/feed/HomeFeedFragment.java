package be.ugent.zeus.hydra.feed;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.AdapterObserver;
import be.ugent.zeus.hydra.common.arch.observers.ErrorObserver;
import be.ugent.zeus.hydra.common.main.MainActivity;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.customtabs.CustomTabsHelper;
import be.ugent.zeus.hydra.common.ui.recyclerview.SpanItemSpacingDecoration;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.minerva.announcement.SingleAnnouncementActivity;
import be.ugent.zeus.hydra.minerva.announcement.courselist.AnnouncementsForCourseFragment;
import be.ugent.zeus.hydra.feed.commands.FeedCommand;

import static android.app.Activity.RESULT_OK;
import static be.ugent.zeus.hydra.feed.FeedLiveData.REFRESH_HOMECARD_TYPE;

/**
 * The fragment showing the home feed.
 *
 * The user has the possibility to decide to hide certain card types. When a user disables a certain type of cards,
 * we do not retrieve the data.
 *
 * Getting the home feed data is not very simple, mainly because we want partial updates. The home feed consists of a
 * bunch of {@link HomeFeedRequest}s that are executed, and the result is shown in the RecyclerView. As there can be up
 * to 9 requests, we can't just load everything and then display it at once; this would show an empty screen for a long
 * time.
 *
 * Instead, we insert data to the RecyclerView as soon the a request is completed. TODO documentation
 *
 * @author Niko Strijbol
 * @author silox
 */
public class HomeFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, HomeFeedAdapter.AdapterCompanion, MainActivity.ScheduledRemovalListener {

    private static final String TAG = "HomeFeedFragment";

    public static final String PREF_DISABLED_CARD_TYPES = "pref_disabled_cards";
    public static final String PREF_DISABLED_CARD_HACK = "pref_disabled_specials_hack";

    public static final int REQUEST_HOMECARD_ID = 5050;

    private boolean firstRun;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActivityHelper helper;
    private Snackbar snackbar;
    private FeedViewModel model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        helper = CustomTabsHelper.initHelper(getActivity(), null);
        helper.setShareMenu();
    }

    public ActivityHelper getHelper() {
        return helper;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.home_cards_view);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.ugent_yellow_dark);

        HomeFeedAdapter adapter = new HomeFeedAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(getContext()));
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DismissCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);

        model = ViewModelProviders.of(this).get(FeedViewModel.class);
        model.getData().observe(this, ErrorObserver.with(this::onError));
        model.getData().observe(this, new AdapterObserver<>(adapter));
        model.getData().observe(this, data -> {
            if (data != null && data.hasData()) {
                if (data.isDone()) {
                    firstRun = false;
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (firstRun) {
                        recyclerView.scrollToPosition(0);
                    }
                }
            }
        });

        model.getRefreshing().observe(this, swipeRefreshLayout::setRefreshing);

        // Observe results from commands.
        model.getCommandLiveEvent().observe(this, Runnable::run);

        firstRun = true;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //See https://code.google.com/p/android/issues/detail?id=78062
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.clearAnimation();
    }

    @Override
    public void onRemovalScheduled() {
        // If we are removing the fragment, hide any snackbars
        if (this.snackbar != null) {
            this.snackbar.dismiss();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
        //TODO there must a better of doing this
        BaseActivity activity = (BaseActivity) getActivity();
        BaseActivity.tintToolbarIcons(activity.getToolbar(), menu, R.id.action_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh) {
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        model.requestRefresh();
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        if (snackbar != null) {
            snackbar.dismiss();
        }
        snackbar = Snackbar.make(getView(), getString(R.string.home_feed_failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> onRefresh());
        snackbar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_HOMECARD_ID && resultCode == RESULT_OK) {
            // Something was launched and we received a result back.
            if (data.getBooleanExtra(AnnouncementsForCourseFragment.RESULT_ANNOUNCEMENT_UPDATED, false)
                    || data.getBooleanExtra(SingleAnnouncementActivity.RESULT_ANNOUNCEMENT_READ, false)) {
                Bundle extras = new Bundle();
                extras.putInt(REFRESH_HOMECARD_TYPE, Card.Type.MINERVA_ANNOUNCEMENT);
                model.requestRefresh(extras);
            }
        }
    }

    @Override
    public int getRequestCode() {
        return REQUEST_HOMECARD_ID;
    }

    @Override
    public void executeCommand(FeedCommand command) {
        new CommandTask(model.getCommandLiveEvent()) {
            @Override
            protected Integer doInBackground(Void... voids) {
                return command.execute(getContext());
            }

            @Override
            protected void onSafePostExecute(int cardType) {
                Bundle extras = new Bundle();
                extras.putInt(REFRESH_HOMECARD_TYPE, cardType);
                model.requestRefresh(extras);

                if (snackbar != null) {
                    snackbar.dismiss();
                }
                snackbar = Snackbar.make(getView(), command.getCompleteMessage(), BaseTransientBottomBar.LENGTH_LONG)
                        .setAction(R.string.home_feed_undo, view -> undoCommand(command));
                snackbar.show();
            }
        }.execute();
    }

    private void undoCommand(FeedCommand command) {
        new CommandTask(model.getCommandLiveEvent()) {
            @Override
            protected Integer doInBackground(Void... voids) {
                return command.undo(getContext());
            }

            @Override
            protected void onSafePostExecute(int cardType) {
                Bundle extras = new Bundle();
                extras.putInt(REFRESH_HOMECARD_TYPE, cardType);
                model.requestRefresh(extras);
            }
        }.execute();
    }

    private abstract static class CommandTask extends AsyncTask<Void, Void, Integer> {

        private final MutableLiveData<Runnable> bus;

        private CommandTask(MutableLiveData<Runnable> bus) {
            this.bus = bus;
        }

        @Override
        protected final void onPostExecute(Integer integer) {
            bus.postValue(() -> onSafePostExecute(integer));
        }

        protected abstract void onSafePostExecute(int cardType);
    }
}