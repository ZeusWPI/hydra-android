package be.ugent.zeus.hydra.ui.main.homefeed.content.specialevent;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.data.network.requests.SpecialRemoteEventRequest;
import be.ugent.zeus.hydra.repository.requests.Requests;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedFragment;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.utils.PreferencesUtils;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Request wrapper to limit the number of requests that are shown.
 *
 * @author Niko Strijbol
 */
public class SpecialEventRequest implements HomeFeedRequest {

    private final SpecialRemoteEventRequest remoteEventRequest;
    private final Context context;

    public SpecialEventRequest(Context context) {
        this.remoteEventRequest = new SpecialRemoteEventRequest(context,
                Requests.cache(context, new be.ugent.zeus.hydra.data.network.requests.SpecialEventRequest())
        );
        this.context = context;
    }

    @NonNull
    @Override
    public Result<Stream<HomeCard>> performRequest(Bundle args) {

        ZonedDateTime now = ZonedDateTime.now();
        return remoteEventRequest.performRequest(args).map(specialEventWrapper -> {
            List<HomeCard> list = new ArrayList<>();
            Set<String> hidden = PreferencesUtils.getStringSet(context, HomeFeedFragment.PREF_DISABLED_SPECIALS);

            for (SpecialEvent event : specialEventWrapper.getSpecialEvents()) {
                if ((event.getStart() == null && event.getEnd() == null) // TODO: remove this condition
                        || (event.getStart().isBefore(now) && event.getEnd().isAfter(now))
                        || (BuildConfig.DEBUG && event.isDevelopment())) {

                    if (!hidden.contains(String.valueOf(event.getId()))) {
                        list.add(new SpecialEventCard(event));
                    }
                    hidden.remove(String.valueOf(event.getId()));
                }
            }

            // The ids that are still there means that they aren't displayed anymore, and so can be removed.
            PreferencesUtils.removeFromStringSet(context, HomeFeedFragment.PREF_DISABLED_SPECIALS, hidden);

            return StreamSupport.stream(list);
        });
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.SPECIAL_EVENT;
    }
}