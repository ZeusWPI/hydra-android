package be.ugent.zeus.hydra.ui.main.homefeed.loader;

import android.content.Context;
import android.support.annotation.UiThread;
import android.support.v4.app.LoaderManager;
import android.util.Pair;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.ui.main.homefeed.feed.FeedOperation;
import be.ugent.zeus.hydra.utils.IterableSparseArray;

import java.util.List;
import java.util.Set;

/**
 * A instance of this interface can receive partial updates from the {@link HomeFeedLoader}.
 *
 * @author Niko Strijbol
 */
public interface HomeFeedLoaderCallback extends LoaderManager.LoaderCallbacks<Pair<Set<Integer>, List<HomeCard>>> {

    IterableSparseArray<FeedOperation> onScheduleOperations(Context context);

    /**
     * This is called when the adapter receives new data.
     *
     * Note that the adapter does NOT receive an update if the data was cached by the loader.
     *
     * @param data The new data for the adapter. This is the full data, not a partial result.
     * @param cardType The type of card that was updated.
     */
    @UiThread
    void onPartialUpdate(List<HomeCard> data, @HomeCard.CardType int cardType);

    @UiThread
    void onPartialError(@HomeCard.CardType int cardType);
}