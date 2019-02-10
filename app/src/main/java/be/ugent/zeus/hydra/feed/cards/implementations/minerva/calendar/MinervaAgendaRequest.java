package be.ugent.zeus.hydra.feed.cards.implementations.minerva.calendar;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.database.RepositoryFactory;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardRepository;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItem;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItemRepository;
import java9.util.stream.Collectors;
import java9.util.stream.Stream;
import java9.util.stream.StreamSupport;
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
    protected Result<Stream<Card>> performRequestCards(@NonNull Bundle args) {
        OffsetDateTime now = OffsetDateTime.now();
        // Only display things up to 3 weeks from now.
        OffsetDateTime oneMonth = now.plusWeeks(3);
        //Note: in real Java 8 streams, we could concat the operations below.
        //Sort the items per day
        Map<LocalDate, List<AgendaItem>> perDay = StreamSupport.stream(dao.getBetweenNonIgnored(now, oneMonth))
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