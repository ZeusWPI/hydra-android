package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.utils.Objects;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

import java.util.List;

/**
 * Home card for {@link Announcement}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class MinervaAnnouncementsCard extends HomeCard {

    private final List<Announcement> announcement;
    private final Course course;

    public MinervaAnnouncementsCard(List<Announcement> announcement, Course course) {
        this.announcement = announcement;
        this.course = course;
    }

    @Override
    public int getPriority() {
        ZonedDateTime date = this.getAnnouncements().get(0).getDate();
        Duration duration = Duration.between(date, ZonedDateTime.now());
        return (int) (1000 - (duration.toDays() * 100));
    }

    @Override
    @HomeCard.CardType
    public int getCardType() {
        return CardType.MINERVA_ANNOUNCEMENT;
    }

    public List<Announcement> getAnnouncements() {
        return announcement;
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MinervaAnnouncementsCard that = (MinervaAnnouncementsCard) o;
        return Objects.equals(announcement, that.announcement) &&
                Objects.equals(course, that.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(announcement, course);
    }
}