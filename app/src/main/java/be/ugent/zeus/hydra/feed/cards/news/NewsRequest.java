package be.ugent.zeus.hydra.feed.cards.news;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;
import be.ugent.zeus.hydra.news.NewsStream;

/**
 * @author Niko Strijbol
 */
public class NewsRequest extends HideableHomeFeedRequest {

    private final Request<NewsStream> request;

    public NewsRequest(Context context, DismissalDao dismissalDao) {
        super(dismissalDao);
        this.request = new be.ugent.zeus.hydra.news.NewsRequest(context);
    }

    @Override
    public int getCardType() {
        return Card.Type.NEWS_ITEM;
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@NonNull Bundle args) {
        return request.execute(args).map(s -> s.getEntries()
                .stream()
                .map(NewsItemCard::new));
    }
}
