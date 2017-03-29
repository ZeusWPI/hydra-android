package be.ugent.zeus.hydra.ui.main.homefeed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.ui.common.customtabs.CustomTabsHelper;
import be.ugent.zeus.hydra.ui.common.plugins.OfflinePlugin;
import be.ugent.zeus.hydra.ui.common.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.common.plugins.common.PluginFragment;
import be.ugent.zeus.hydra.ui.common.recyclerview.SpanItemSpacingDecoration;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.ui.main.homefeed.loader.HomeFeedLoader;
import be.ugent.zeus.hydra.ui.main.homefeed.loader.LoaderResult;

import java.util.List;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

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
 * If the data is cached in the loader, e.g. a rotation has occurred, the data is delivered directly to
 * {@link #onLoadFinished(Loader, LoaderResult)}, without the partial updates.
 *
 * @author Niko Strijbol
 * @author silox
 */
public class HomeFeedFragment extends PluginFragment implements LoaderManager.LoaderCallbacks<LoaderResult>, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "HomeFeedFragment";

    public static final String PREF_DISABLED_CARDS = "pref_disabled_cards";

    private static final int LOADER = 0;

    private boolean firstRun;

    private SwipeRefreshLayout swipeRefreshLayout;
    private HomeFeedAdapter adapter;
    private RecyclerView recyclerView;

    private ActivityHelper helper;

    private OfflinePlugin plugin = new OfflinePlugin(this);

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugins.add(plugin);
    }

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

        recyclerView = $(view, R.id.home_cards_view);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout = $(view, R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.ugent_yellow_dark);

        adapter = new HomeFeedAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(getContext()));
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setRefreshing(true);
        getLoaderManager().initLoader(LOADER, null, this);
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

    private void showErrorMessage(String message) {
        //noinspection WrongConstant
       plugin.showSnackbar(message, Snackbar.LENGTH_LONG);
    }

    public HomeFeedLoader getLoader() {
        return (HomeFeedLoader) getLoaderManager().<LoaderResult>getLoader(LOADER);
    }

    public void onPartialError(@HomeCard.CardType int cardType) {
        showErrorMessage(getContext().getString(R.string.home_feed_not_loaded));
    }

    @Override
    public Loader<LoaderResult> onCreateLoader(int id, Bundle args) {
        return new HomeFeedLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult> l, LoaderResult data) {
        Log.i(TAG, "Finished loading data");

        adapter.setItems(data.getData());

        //TODO: error handling

        //Scroll to top if not refreshed
        if (data.isCompleted()) {
            firstRun = false;
            swipeRefreshLayout.setRefreshing(false);
        } else {
            if (firstRun) {
                recyclerView.scrollToPosition(0);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult> loader) {
        //Do nothing
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
        plugin.dismiss();
        swipeRefreshLayout.setRefreshing(true);
        getLoader().flagForRefresh();
        getLoader().onContentChanged();
    }
}