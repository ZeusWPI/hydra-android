package be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.announcement;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.database.RepositoryFactory;
import be.ugent.zeus.hydra.domain.models.feed.Card;
import be.ugent.zeus.hydra.domain.repository.AnnouncementRepository;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

/**
 * @author Niko Strijbol
 */
public class MinervaAnnouncementRequest implements HomeFeedRequest {

    private final AnnouncementRepository dao;

    public MinervaAnnouncementRequest(Context context) {
        this.dao = RepositoryFactory.getAnnouncementRepository(context);
    }

    @NonNull
    @Override
    public Result<Stream<Card>> performRequest(Bundle args) {
        return Result.Builder.fromData(StreamSupport.stream(dao.getMostRecentFirstMap().entrySet())
                .map(s -> new MinervaAnnouncementsCard(s.getValue(), s.getKey())));
    }

    @Override
    public int getCardType() {
        return Card.Type.MINERVA_ANNOUNCEMENT;
    }
}