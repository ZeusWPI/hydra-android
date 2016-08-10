package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.SpecialEventCard;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.models.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.SpecialEventRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Callback for special events.
 *
 * @author Niko Strijbol
 */
class SpecialEventCallback extends HomeLoaderCallback<SpecialEventWrapper> {

    private static final boolean DEVELOPMENT = BuildConfig.DEBUG;

    public SpecialEventCallback(Context context, HomeCardAdapter adapter, ProgressCallback callback) {
        super(context, adapter, callback);
    }

    @Override
    protected SpecialEventRequest getCacheRequest() {
        return new SpecialEventRequest();
    }

    @Override
    protected List<HomeCard> convertData(@NonNull SpecialEventWrapper data) {
        List<HomeCard> list = new ArrayList<>();
        for (SpecialEvent event: data.getSpecialEvents()) {
            Date now = new Date();
            if ((event.getStart().before(now) && event.getEnd().after(now)) || (DEVELOPMENT && event.isDevelopment())) {
                list.add(new SpecialEventCard(event));
            }
        }
        return list;
    }

    /**
     * @return The card type of the cards that are produced here.
     */
    @Override
    protected int getCardType() {
        return HomeCard.CardType.SPECIAL_EVENT;
    }
}