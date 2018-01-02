package be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.announcement;

import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedUtils;
import be.ugent.zeus.hydra.domain.models.minerva.Announcement;
import be.ugent.zeus.hydra.domain.models.minerva.Course;
import java8.util.Objects;
import org.threeten.bp.Duration;
import org.threeten.bp.OffsetDateTime;
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
        OffsetDateTime date = getAnnouncements().get(0).getDate();
        Duration duration = Duration.between(date, ZonedDateTime.now());
        return FeedUtils.lerp((int) duration.toHours(), 0, 1488);
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