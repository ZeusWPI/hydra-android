package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.List;

/**
 * Created by feliciaan on 30/06/16.
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
        DateTime date = new DateTime(this.getAnnouncements().get(0).getDate());
        Duration duration = new Duration(date, new DateTime());
        return (int) (1000 - (duration.getStandardDays() * 100));
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
}
