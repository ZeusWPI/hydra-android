package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loaders.AbstractAsyncLoader;
import be.ugent.zeus.hydra.loaders.LoaderException;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.SpecialEventCard;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.models.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.SpecialRemoteEventRequest;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback for special events.
 *
 * @author Niko Strijbol
 */
class SpecialEventCallback extends HomeLoaderCallback {

    private static final boolean DEVELOPMENT = BuildConfig.DEBUG;

    public SpecialEventCallback(Context context, HomeCardAdapter adapter, FragmentCallback callback) {
        super(context, adapter, callback);
    }

    @Override
    protected int getErrorName() {
        return R.string.fragment_home_error_special;
    }

    @Override
    protected int getCardType() {
        return HomeCard.CardType.SPECIAL_EVENT;
    }

    @Override
    public Loader<ThrowableEither<List<HomeCard>>> onCreateLoader(int id, Bundle args) {
        return new SpecialAsyncLoader(context);
    }

    private static class SpecialAsyncLoader extends AbstractAsyncLoader<List<HomeCard>> {

        private SpecialAsyncLoader(Context context) {
            super(context);
        }

        @NonNull
        @Override
        protected List<HomeCard> getData() throws LoaderException {
            try {
                return convertData((new SpecialRemoteEventRequest()).performRequest());
            } catch (RequestFailureException e) {
                throw new LoaderException(e);
            }
        }

        private List<HomeCard> convertData(@NonNull SpecialEventWrapper data) {
            List<HomeCard> list = new ArrayList<>();
            ZonedDateTime now = ZonedDateTime.now();
            for (SpecialEvent event: data.getSpecialEvents()) {

                //Events without date are always shown.
                if(event.getStart() == null && event.getEnd() == null) {
                    list.add(new SpecialEventCard(event));
                } else {
                    if ((event.getStart().isBefore(now) && event.getEnd().isAfter(now)) || (DEVELOPMENT && event.isDevelopment())) {
                        list.add(new SpecialEventCard(event));
                    }
                }
            }
            return list;
        }
    }
}