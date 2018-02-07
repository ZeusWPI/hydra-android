package be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.announcement;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.data.database.RepositoryFactory;
import be.ugent.zeus.hydra.domain.models.feed.Card;
import be.ugent.zeus.hydra.domain.repository.AnnouncementRepository;
import be.ugent.zeus.hydra.domain.repository.CardRepository;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.HideableHomeFeedRequest;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

/**
 * @author Niko Strijbol
 */
public class MinervaAnnouncementRequest extends HideableHomeFeedRequest {

    private final AnnouncementRepository dao;

    public MinervaAnnouncementRequest(Context context, CardRepository cardRepository) {
        super(cardRepository);
        this.dao = RepositoryFactory.getAnnouncementRepository(context);
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@Nullable Bundle args) {
        return Result.Builder.fromData(StreamSupport.stream(dao.getMostRecentFirstMap().entrySet())
                .map(s -> new MinervaAnnouncementsCard(s.getValue(), s.getKey())));
    }

    @Override
    public int getCardType() {
        return Card.Type.MINERVA_ANNOUNCEMENT;
    }
}