package be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.announcement;

import android.support.annotation.Size;

import be.ugent.zeus.hydra.feed.Card;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedUtils;
import be.ugent.zeus.hydra.minerva.Announcement;
import be.ugent.zeus.hydra.minerva.Course;
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

    MinervaAnnouncementsCard(@Size(min = 1) List<Announcement> announcements, Course course) {
        if (announcements.isEmpty()) {
            throw new IllegalStateException("The announcement card cannot be empty.");
        }
        this.announcement = announcements;
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
        // We use the course and the ID of the most recent announcement. This way, a card will return
        // if a new announcement appears for some course.
        return course.getId() + announcement.get(0).getItemId();
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