package be.ugent.zeus.hydra.minerva.course.database;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

/**
 * Contains a course and the number of unread announcements for that course.
 *
 * @author Niko Strijbol
 */
final class CourseUnread {

    @Embedded
    private CourseDTO course;
    @ColumnInfo(name = "unread_count")
    private long unreadAnnouncements;

    public CourseDTO getCourse() {
        return course;
    }

    public long getUnreadAnnouncements() {
        return unreadAnnouncements;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public void setUnreadAnnouncements(long unreadAnnouncements) {
        this.unreadAnnouncements = unreadAnnouncements;
    }
}