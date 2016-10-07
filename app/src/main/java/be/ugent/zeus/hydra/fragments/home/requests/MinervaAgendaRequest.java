package be.ugent.zeus.hydra.fragments.home.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.minerva.agenda.AgendaDao;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.MinervaAgendaCard;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.List;

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

        for (AgendaItem item: list) {
            cards.add(new MinervaAgendaCard(item));
        }

        return cards;
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.MINERVA_AGENDA;
    }
}