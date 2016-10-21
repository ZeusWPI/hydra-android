package be.ugent.zeus.hydra.fragments.home.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.minerva.agenda.AgendaDao;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.MinervaAgendaCard;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Request for Minerva agenda items.
 *
 * @author Niko Strijbol
 */
public class MinervaAgendaRequest implements Request<List<HomeCard>>, HomeFeedRequest {

    private final AgendaDao dao;

    public MinervaAgendaRequest(Context context) {
        this.dao = new AgendaDao(context);
    }

    @NonNull
    @Override
    public List<HomeCard> performRequest() throws RequestFailureException {
        List<AgendaItem> list = dao.getFutureAgenda(Instant.now());
        List<HomeCard> cards = new ArrayList<>();

        //Sort the agenda items per day
        Map<LocalDate, List<AgendaItem>> map = new HashMap<>();

        for (AgendaItem item: list) {

            if(!map.containsKey(DateUtils.toLocalDateTime(item.getStartDate()).toLocalDate())) {
                map.put(DateUtils.toLocalDateTime(item.getStartDate()).toLocalDate(), new ArrayList<AgendaItem>());
            }

            map.get(DateUtils.toLocalDateTime(item.getStartDate()).toLocalDate()).add(item);
        }

        for (Map.Entry<LocalDate, List<AgendaItem>> itemEntry: map.entrySet()) {
            cards.add(new MinervaAgendaCard(itemEntry.getKey(), itemEntry.getValue()));
        }

        return cards;
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.MINERVA_AGENDA;
    }
}