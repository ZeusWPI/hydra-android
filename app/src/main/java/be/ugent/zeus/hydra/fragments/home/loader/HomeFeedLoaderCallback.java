package be.ugent.zeus.hydra.fragments.home.loader;

import android.support.annotation.UiThread;
import android.support.v4.app.LoaderManager;
import android.util.Pair;
import be.ugent.zeus.hydra.models.cards.HomeCard;

import java.util.List;
import java.util.Set;

/**
 * A instance of this interface can receive partial updates from the {@link HomeFeedLoader}.
 *
 * @author Niko Strijbol
 */
public interface HomeFeedLoaderCallback extends LoaderManager.LoaderCallbacks<Pair<Set<Integer>, List<HomeCard>>>{

    /**
     * This is called when the adapter receives an update.
     *
     * @param cardType The type of card that was updated.
     */
    @UiThread
    void onNewDataUpdate(@HomeCard.CardType int cardType);

    @UiThread
    void onPartialError(@HomeCard.CardType int cardType);
}