package be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.agenda;

import be.ugent.zeus.hydra.domain.models.feed.Card;
import be.ugent.zeus.hydra.domain.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedUtils;
import java8.util.Objects;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.List;

/**
 * Home card for {@link AgendaItem}.
 *
 * @author Niko Strijbol
 */
class MinervaAgendaCard extends Card {

    // From 11 h to 15 h we are more interested in the menu.
    private static final LocalDateTime disInterestStart = LocalDateTime.now().withHour(11).withMinute(0);
    private static final LocalDateTime disInterestEnd = LocalDateTime.now().withHour(15).withMinute(0);

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
        LocalDateTime now = LocalDateTime.now();
        int duration = (int) ChronoUnit.DAYS.between(now.toLocalDate(), date);
        if (!(now.isAfter(disInterestStart) && now.isBefore(disInterestEnd))) {
            return FeedUtils.lerp(duration, 0, 21) - 5;
        } else {
            return FeedUtils.lerp(duration, 0, 21);
        }
    }

    @Override
    public String getIdentifier() {
        // We say this is the same card if it is for the same day.
        return date.toString();
    }

    @Override
    @Card.Type
    public int getCardType() {
        return Card.Type.MINERVA_AGENDA;
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