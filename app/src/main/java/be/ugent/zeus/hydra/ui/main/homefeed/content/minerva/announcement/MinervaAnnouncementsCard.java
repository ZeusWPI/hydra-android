package be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.announcement;

import be.ugent.zeus.hydra.domain.models.feed.Card;
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
class MinervaAnnouncementsCard extends Card {

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
    public String getIdentifier() {
        // We say this is the same if it is for the same course.
        // TODO: this is not correct, but we don't support it currently.
        return course.getId();
    }

    @Override
    @Card.Type
    public int getCardType() {
        return Card.Type.MINERVA_ANNOUNCEMENT;
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