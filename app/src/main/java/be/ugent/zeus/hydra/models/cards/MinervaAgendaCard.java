package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.utils.Objects;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;

import java.util.List;

/**
 * Home card for {@link AgendaItem}.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinervaAgendaCard that = (MinervaAgendaCard) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(agenda, that.agenda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, agenda);
    }
}