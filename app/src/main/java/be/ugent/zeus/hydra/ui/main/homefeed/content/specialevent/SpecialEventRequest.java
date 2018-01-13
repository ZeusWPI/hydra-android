package be.ugent.zeus.hydra.ui.main.homefeed.content.specialevent;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.feed.Card;
import be.ugent.zeus.hydra.feed.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.feed.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.feed.CardRepository;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Requests;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.HideableHomeFeedRequest;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Request wrapper to limit the number of requests that are shown.
 *
 * @author Niko Strijbol
 */
public class SpecialEventRequest extends HideableHomeFeedRequest {

    private final Request<SpecialEventWrapper> remoteEventRequest;

    public SpecialEventRequest(Context context, CardRepository cardRepository) {
        super(cardRepository);
        this.remoteEventRequest = Requests.cache(context, new be.ugent.zeus.hydra.feed.specialevent.SpecialEventRequest());
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@Nullable Bundle args) {
        ZonedDateTime now = ZonedDateTime.now();
        return remoteEventRequest.performRequest(args).map(specialEventWrapper -> {
            List<Card> list = new ArrayList<>();

            List<SpecialEvent> specialEvents = new ArrayList<>(specialEventWrapper.getSpecialEvents());

            // This is for local debug purposes
            if (BuildConfig.DEBUG_HOME_STREAM_ADD_SKO_CARD) {
                specialEvents.add(buildDebugSko());
            }

            for (SpecialEvent event : specialEvents) {
                if ((event.getStart() == null && event.getEnd() == null)
                        || (event.getStart().isBefore(now) && event.getEnd().isAfter(now))
                        || (BuildConfig.DEBUG && event.isDevelopment())) {
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