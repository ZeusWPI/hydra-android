package be.ugent.zeus.hydra.fragments.home.operations;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.util.Pair;
import be.ugent.zeus.hydra.fragments.home.loader.HomeDiffCallback;
import be.ugent.zeus.hydra.fragments.home.requests.HomeFeedRequest;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;

import java.util.*;

/**
 * An operation that adds items to the home feed.
 *
 * @author Niko Strijbol
 */
public class RequestOperation implements FeedOperation {

    private final HomeFeedRequest request;

    public RequestOperation(HomeFeedRequest request) {
        this.request = request;
    }

    public static RequestOperation add(HomeFeedRequest request) {
        return new RequestOperation(request);
    }

    @NonNull
    @Override
    public Pair<List<HomeCard>, DiffUtil.DiffResult> transform(List<HomeCard> current) throws RequestFailureException {

        final List<HomeCard> newData = new ArrayList<>(current);
        FeedUtils.addAndSort(newData, request.performRequest(), request.getCardType());

        final DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new HomeDiffCallback(current, newData), true);

        return new Pair<>(newData, diff);
    }

    @Override
    public int getCardType() {
        return request.getCardType();
    }
}