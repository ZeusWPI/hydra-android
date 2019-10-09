package be.ugent.zeus.hydra.feed;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.AdapterObserver;
import be.ugent.zeus.hydra.common.arch.observers.EventObserver;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.customtabs.CustomTabsHelper;
import be.ugent.zeus.hydra.common.ui.recyclerview.SpanItemSpacingDecoration;
import be.ugent.zeus.hydra.feed.commands.CommandResult;
import be.ugent.zeus.hydra.feed.commands.FeedCommand;
import com.google.android.material.snackbar.Snackbar;

import static be.ugent.zeus.hydra.feed.FeedLiveData.REFRESH_HOMECARD_TYPE;
import static be.ugent.zeus.hydra.utils.FragmentUtils.requireBaseActivity;

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
 * Instead, we insert data to the RecyclerView as soon the a request is completed.
 *
 * @author Niko Strijbol
 * @author silox
 */
public class HomeFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, HomeFeedAdapter.AdapterCompanion, MainActivity.ScheduledRemovalListener {

    private static final String TAG = "HomeFeedFragment";

    public static final String PREF_DISABLED_CARD_TYPES = "pref_disabled_cards";
    // TODO: replace this by proper listener to database.
    public static final String PREF_DISABLED_CARD_HACK = "pref_disabled_specials_hack";

    private static final int REQUEST_HOMECARD_ID = 5050;

    private boolean firstRun;
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

    @Override
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
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.hydra_secondary_colour);

        HomeFeedAdapter adapter = new HomeFeedAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(requireContext()));
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DismissCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);

        model = ViewModelProviders.of(this).get(FeedViewModel.class);
        // Basically the same as PartialErrorObserver, but we only observe at the end.
        model.getData().observe(this, result -> {
            if (result != null && result.isDone() && result.hasException()) {
                onError(result.getError());
            }
        });
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

        // Monitor commands
        model.getCommandLiveData().observe(this, EventObserver.with(this::onCommandExecuted));

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
    public void onRemovalScheduled() {
        // If we are removing the fragment, hide any snackbars
        if (this.snackbar != null) {
            this.snackbar.dismiss();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
        requireBaseActivity(this).tintToolbarIcons(menu, R.id.action_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh) {
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
        snackbar = Snackbar.make(requireView(), getString(R.string.feed_general_error), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> onRefresh());
        snackbar.show();
    }

    @Override
    public int getRequestCode() {
        return REQUEST_HOMECARD_ID;
    }

    @Override
    public void executeCommand(FeedCommand command) {
        model.execute(command);
    }

    private void onCommandExecuted(CommandResult result) {
        Bundle extras = new Bundle();
        extras.putInt(REFRESH_HOMECARD_TYPE, result.getCardType());
        model.requestRefresh(extras);

        // If it is the undoing, don't show a snackbar, otherwise do show it.
        if (!result.wasUndo()) {
            if (snackbar != null) {
                snackbar.dismiss();
            }
            FeedCommand command = result.getCommand();
            assert getView() != null;
            snackbar = Snackbar.make(getView(), command.getCompleteMessage(), Snackbar.LENGTH_LONG)
                    .setAction(command.getUndoMessage(), view -> model.undo(command));
            snackbar.show();
        }
    }
}
