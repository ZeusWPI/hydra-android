package be.ugent.zeus.hydra.fragments.home.loader;

import android.support.annotation.UiThread;
import android.support.v4.app.LoaderManager;

import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.utils.IterableSparseArray;

import java.util.List;

/**
 * A instance of this interface can receive partial updates from the {@link HomeFeedLoader}.
 *
 * @author Niko Strijbol
 */
public interface HomeFeedLoaderCallback extends LoaderManager.LoaderCallbacks<IterableSparseArray<ThrowableEither<List<HomeCard>>>>{

    @UiThread
    void onPartialResult(List<HomeCard> data, @HomeCard.CardType int cardType);

    @UiThread
    void onPartialError(@HomeCard.CardType int cardType);
}