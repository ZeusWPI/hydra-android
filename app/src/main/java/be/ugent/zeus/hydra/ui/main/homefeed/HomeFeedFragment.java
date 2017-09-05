package be.ugent.zeus.hydra.ui.main.homefeed;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.repository.observers.AdapterObserver;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.ui.common.customtabs.CustomTabsHelper;
import be.ugent.zeus.hydra.ui.common.recyclerview.SpanItemSpacingDecoration;
import be.ugent.zeus.hydra.ui.main.homefeed.commands.FeedCommand;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.ui.minerva.AnnouncementActivity;
import be.ugent.zeus.hydra.ui.minerva.overview.CourseActivity;

import static android.app.Activity.RESULT_OK;
import static be.ugent.zeus.hydra.ui.main.homefeed.FeedLiveData.REFRESH_HOMECARD_TYPE;

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
public class HomeFeedFragment extends LifecycleFragment implements SwipeRefreshLayout.OnRefreshListener, HomeFeedAdapter.AdapterCompanion {

    private static final String TAG = "HomeFeedFragment";

    public static final String PREF_DISABLED_CARDS = "pref_disabled_cards";
    public static final String PREF_DISABLED_SPECIALS = "pref_disabled_specials";

    public static final int REQUEST_HOMECARD_ID = 5050;

    private boolean firstRun;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActivityHelper helper;
    private Snackbar errorBar;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
        if (errorBar == null) {
            errorBar = Snackbar.make(getView(), getString(R.string.home_feed_failure), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.again), v -> onRefresh());
        }

        errorBar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_HOMECARD_ID && resultCode == RESULT_OK) {
            // Something was launched and we received a result back.
            if (data.getBooleanExtra(CourseActivity.RESULT_ANNOUNCEMENT_UPDATED, false)
                    || data.getBooleanExtra(AnnouncementActivity.RESULT_ANNOUNCEMENT_READ, false)) {
                Bundle extras = new Bundle();
                extras.putInt(REFRESH_HOMECARD_TYPE, HomeCard.CardType.MINERVA_ANNOUNCEMENT);
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

        // TODO: should this be done on a separate thread?

        int cardType = command.execute(getContext());
        Bundle extras = new Bundle();
        extras.putInt(REFRESH_HOMECARD_TYPE, cardType);
        model.requestRefresh(extras);

        Snackbar.make(getView(), command.getCompleteMessage(), BaseTransientBottomBar.LENGTH_LONG)
                .setAction(R.string.home_feed_undo, view -> undoCommand(command))
                .show();
    }

    private void undoCommand(FeedCommand command) {
        int cardType = command.undo(getContext());
        Bundle extras = new Bundle();
        extras.putInt(REFRESH_HOMECARD_TYPE, cardType);
        model.requestRefresh(extras);
    }


}