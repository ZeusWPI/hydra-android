package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import org.threeten.bp.*;

/**
 * Card to show Minerva agenda item.
 *
 * @author Niko Strijbol
 */
public class MinervaAgendaCard extends HomeCard {

    private final AgendaItem agenda;

    public MinervaAgendaCard(AgendaItem announcement) {
        this.agenda = announcement;
    }

    @Override
    public int getPriority() {
        Duration duration = Duration.between(ZonedDateTime.now(), agenda.getStartDate());
        return 950 - Math.max(0, (int) duration.toHours()) * 4; //see 10 days in to the future
    }

    public AgendaItem getAgendaItem() {
        return agenda;
    }

    @Override
    @CardType
    public int getCardType() {
        return CardType.MINERVA_AGENDA;
    }
}
