package be.ugent.zeus.hydra.minerva.calendar.feed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.common.database.RepositoryFactory;
import be.ugent.zeus.hydra.feed.Card;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItem;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItemRepository;
import be.ugent.zeus.hydra.feed.CardRepository;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.HideableHomeFeedRequest;
import java8.util.stream.Collectors;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;

import java.util.List;
import java.util.Map;

/**
 * Request for Minerva agenda items.
 *
 * @author Niko Strijbol
 */
public class MinervaAgendaRequest extends HideableHomeFeedRequest {

    private final AgendaItemRepository dao;

    public MinervaAgendaRequest(Context context, CardRepository cardRepository) {
        super(cardRepository);
        this.dao = RepositoryFactory.getAgendaItemRepository(context);
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@Nullable Bundle args) {
        OffsetDateTime now = OffsetDateTime.now();
        // Only display things up to 3 weeks from now.
        OffsetDateTime oneMonth = now.plusWeeks(3);
        //Note: in real Java 8 streams, we could concat the operations below.
        //Sort the items per day
        Map<LocalDate, List<AgendaItem>> perDay = StreamSupport.stream(dao.getBetween(now, oneMonth))
                .collect(Collectors.groupingBy(AgendaItem::getLocalStartDate));

        //Convert it to a view
        return Result.Builder.fromData(StreamSupport.stream(perDay.entrySet())
                .map(e -> new MinervaAgendaCard(e.getKey(), e.getValue())));
    }

    @Override
    public int getCardType() {
        return Card.Type.MINERVA_AGENDA;
    }
}