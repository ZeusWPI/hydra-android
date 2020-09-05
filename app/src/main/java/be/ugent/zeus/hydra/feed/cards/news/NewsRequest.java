package be.ugent.zeus.hydra.feed.cards.news;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;
import be.ugent.zeus.hydra.news.UgentNewsArticle;
import be.ugent.zeus.hydra.news.UgentNewsRequest;

/**
 * @author Niko Strijbol
 */
public class NewsRequest extends HideableHomeFeedRequest {

    private final Request<List<UgentNewsArticle>> request;

    public NewsRequest(Context context, DismissalDao dismissalDao) {
        super(dismissalDao);
        this.request = new UgentNewsRequest(context);
    }

    @Override
    public int getCardType() {
        return Card.Type.NEWS_ITEM;
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@NonNull Bundle args) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixMonthsAgo = now.minusWeeks(2);

        return request.execute(args).map(ugentNewsItems -> ugentNewsItems.stream()
                .filter(ugentNewsItem -> sixMonthsAgo.isBefore(ugentNewsItem.getLocalModified()))
                .map(NewsItemCard::new));
    }
}
