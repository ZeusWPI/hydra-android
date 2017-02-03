package be.ugent.zeus.hydra.homefeed.content.minerva.agenda;

import be.ugent.zeus.hydra.homefeed.content.FeedUtils;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import java8.util.Objects;
import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.List;

/**
 * Home card for {@link AgendaItem}.
 *
 * @author Niko Strijbol
 */
class MinervaAgendaCard extends HomeCard {

    private final LocalDate date;
    private final List<AgendaItem> agenda;

    MinervaAgendaCard(LocalDate date, List<AgendaItem> agendaItems) {
        this.date = date;
        this.agenda = agendaItems;
    }

    List<AgendaItem> getAgendaItems() {
        return agenda;
    }

    LocalDate getDate() {
        return date;
    }

    @Override
    public int getPriority() {
        int duration = (int) ChronoUnit.DAYS.between(LocalDate.now(), date);
        return FeedUtils.lerp(duration, 0, 31);
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