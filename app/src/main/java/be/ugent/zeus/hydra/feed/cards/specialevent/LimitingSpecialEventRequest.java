package be.ugent.zeus.hydra.feed.cards.specialevent;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;
import be.ugent.zeus.hydra.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.specialevent.SpecialEventRequest;
import be.ugent.zeus.hydra.specialevent.SpecialEventWrapper;
import java9.util.stream.Stream;
import java9.util.stream.StreamSupport;
import org.threeten.bp.OffsetDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Request wrapper to limit the number of requests that are shown.
 *
 * @author Niko Strijbol
 */
public class LimitingSpecialEventRequest extends HideableHomeFeedRequest {

    private final Request<SpecialEventWrapper> remoteEventRequest;

    public LimitingSpecialEventRequest(Context context, DismissalDao dismissalDao) {
        super(dismissalDao);
        this.remoteEventRequest = new SpecialEventRequest(context);
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@NonNull Bundle args) {
        OffsetDateTime now = OffsetDateTime.now();
        return remoteEventRequest.execute(args).map(specialEventWrapper -> {
            List<Card> list = new ArrayList<>();

            List<SpecialEvent> specialEvents = new ArrayList<>(specialEventWrapper.getSpecialEvents());

            // This is for local debug purposes
            if (BuildConfig.DEBUG_HOME_STREAM_ADD_SKO_CARD) {
                specialEvents.add(buildDebugSko());
            }

            for (SpecialEvent event : specialEvents) {
                if ((event.getStart() == null && event.getEnd() == null)
                        || (event.getStart().isBefore(now) && event.getEnd().isAfter(now))
                        || (BuildConfig.DEBUG && event.isDevelopment())
                        || (BuildConfig.DEBUG && BuildConfig.DEBUG_ENABLE_ALL_SPECIALS)) {
                    list.add(new SpecialEventCard(event));
                }
            }

            return StreamSupport.stream(list);
        });
    }

    @Override
    public int getCardType() {
        return Card.Type.SPECIAL_EVENT;
    }

    private static SpecialEvent buildDebugSko() {
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