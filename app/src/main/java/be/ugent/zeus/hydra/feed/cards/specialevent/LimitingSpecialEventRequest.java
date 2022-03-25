/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.feed.cards.specialevent;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;
import be.ugent.zeus.hydra.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.specialevent.SpecialEventRequest;
import be.ugent.zeus.hydra.specialevent.SpecialEventWrapper;

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

            for (SpecialEvent event : specialEvents) {
                if ((event.getStart() == null && event.getEnd() == null)
                        || (event.getStart().isBefore(now) && event.getEnd().isAfter(now))
                        || (BuildConfig.DEBUG && event.isDevelopment())
                        || (BuildConfig.DEBUG && BuildConfig.DEBUG_ENABLE_ALL_SPECIALS)) {
                    list.add(new SpecialEventCard(event));
                }
            }

            return list.stream();
        });
    }

    @Override
    public int getCardType() {
        return Card.Type.SPECIAL_EVENT;
    }
}