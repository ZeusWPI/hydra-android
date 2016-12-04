package be.ugent.zeus.hydra.homefeed.content.minerva.announcement;

import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import java8.util.Objects;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

import java.util.List;

/**
 * Home card for {@link Announcement}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class MinervaAnnouncementsCard extends HomeCard {

    private final List<Announcement> announcement;
    private final Course course;

    MinervaAnnouncementsCard(List<Announcement> announcement, Course course) {
        this.announcement = announcement;
        this.course = course;
    }

    Course getCourse() {
        return course;
    }

    List<Announcement> getAnnouncements() {
        return announcement;
    }

    @Override
    public int getPriority() {
        ZonedDateTime date = getAnnouncements().get(0).getDate();
        Duration duration = Duration.between(date, ZonedDateTime.now());
        return (int) (1000 - (duration.toDays() * 100));
    }

    @Override
    @HomeCard.CardType
    public int getCardType() {
        return CardType.MINERVA_ANNOUNCEMENT;
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
        return java8.util.Objects.hash(announcement, course);
    }
}