package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.requests.SpecialRemoteEventRequest;
import be.ugent.zeus.hydra.loaders.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.SpecialEventCard;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback for special events.
 *
 * @author Niko Strijbol
 */
class SpecialEventCallback extends AbstractCallback {

    private static final boolean DEVELOPMENT = BuildConfig.DEBUG;

    public SpecialEventCallback(HomeFragment fragment, HomeCardAdapter adapter) {
        super(fragment, adapter);
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
        return new RequestAsyncTaskLoader<>(new SpecialEventRequest(context, fragment.shouldRefresh()), context);
    }

    private static class SpecialEventRequest implements Request<List<HomeCard>> {

        private final SpecialRemoteEventRequest remoteEventRequest;

        private SpecialEventRequest(Context context, boolean shouldRefresh) {
            remoteEventRequest = new SpecialRemoteEventRequest(context, shouldRefresh);
        }

        @NonNull
        @Override
        public List<HomeCard> performRequest() throws RequestFailureException {
            List<HomeCard> list = new ArrayList<>();
            ZonedDateTime now = ZonedDateTime.now();
            for (SpecialEvent event: remoteEventRequest.performRequest().getSpecialEvents()) {

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