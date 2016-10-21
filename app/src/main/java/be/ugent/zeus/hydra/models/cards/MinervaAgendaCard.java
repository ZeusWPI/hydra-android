package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;

import java.util.List;

/**
 * Card to show Minerva agenda item.
 *
 * @author Niko Strijbol
 */
public class MinervaAgendaCard extends HomeCard {

    private final LocalDate date;
    private final List<AgendaItem> agenda;

    public MinervaAgendaCard(LocalDate date, List<AgendaItem> agendaItems) {
        this.date = date;
        this.agenda = agendaItems;
    }

    @Override
    public int getPriority() {
        Period duration = Period.between(LocalDate.now(), date);
        return 1000 - (duration.getDays() * 100);
    }

    public List<AgendaItem> getAgendaItems() {
        return agenda;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    @CardType
    public int getCardType() {
        return CardType.MINERVA_AGENDA;
    }
}
