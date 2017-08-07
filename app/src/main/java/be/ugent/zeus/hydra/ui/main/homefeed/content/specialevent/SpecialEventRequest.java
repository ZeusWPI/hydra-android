package be.ugent.zeus.hydra.ui.main.homefeed.content.specialevent;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.data.models.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.repository.requests.Request;
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

    private final Request<SpecialEventWrapper> remoteEventRequest;
    private final Context context;

    public SpecialEventRequest(Context context) {
        this.remoteEventRequest = Requests.cache(context, new be.ugent.zeus.hydra.data.network.requests.SpecialEventRequest());
        this.context = context;
    }

    @NonNull
    @Override
    public Result<Stream<HomeCard>> performRequest(Bundle args) {

        ZonedDateTime now = ZonedDateTime.now();
        return remoteEventRequest.performRequest(args).map(specialEventWrapper -> {
            List<HomeCard> list = new ArrayList<>();
            Set<String> hidden = PreferencesUtils.getStringSet(context, HomeFeedFragment.PREF_DISABLED_SPECIALS);

            List<SpecialEvent> specialEvents = new ArrayList<>(specialEventWrapper.getSpecialEvents());

            // This is for local debug purposes
            if (BuildConfig.DEBUG_HOME_STREAM_ADD_SKO_CARD) {
                specialEvents.add(buildDebugSko());
            }

            for (SpecialEvent event : specialEvents) {
                if ((event.getStart() == null && event.getEnd() == null)
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

    private SpecialEvent buildDebugSko() {
        SpecialEvent event = new SpecialEvent();
        event.setId(-5);
        event.setName("Student Kick-Off");
        event.setSimpleText("Ga naar de info voor de Student Kick-Off");
        event.setImage("http://blog.studentkickoff.be/wp-content/uploads/2016/07/logo.png");
        event.setPriority(1010);
        event.setInApp(SpecialEvent.SKO_IN_APP);
        event.setDevelopment(true);
        return event;
    }
}