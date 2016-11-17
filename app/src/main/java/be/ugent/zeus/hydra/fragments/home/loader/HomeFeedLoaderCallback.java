package be.ugent.zeus.hydra.fragments.home.loader;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.LoaderManager;
import android.support.v7.util.DiffUtil;
import android.util.Pair;
import be.ugent.zeus.hydra.models.cards.HomeCard;

import java.util.List;
import java.util.Set;

/**
 * A instance of this interface can receive partial updates from the {@link HomeFeedLoader}.
 *
 * @author Niko Strijbol
 */
public interface HomeFeedLoaderCallback extends LoaderManager.LoaderCallbacks<Pair<Set<Integer>, List<HomeCard>>> {

    /**
     * This method is called by the loader before calculating a diff. Care should be taken to ensure that the cards
     * are not updated between a call to this method and the subsequent arrival of data, or some data could be lost.
     *
     * @return The existing data, or an empty list if there is no existing data.
     */
    List<HomeCard> getExistingData();

    /**
     * This is called when the adapter receives an update.
     *
     * @param cardType The type of card that was updated.
     */
    @UiThread
    void onPartialUpdate(List<HomeCard> data, @Nullable DiffUtil.DiffResult updaten, @HomeCard.CardType int cardType);

    @UiThread
    void onPartialError(@HomeCard.CardType int cardType);
}