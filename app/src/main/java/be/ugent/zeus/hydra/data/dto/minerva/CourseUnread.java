package be.ugent.zeus.hydra.data.dto.minerva;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;

/**
 * Contains a course and the number of unread announcements for that course.
 *
 * @author Niko Strijbol
 */
public final class CourseUnread {

    @Embedded
    private CourseDTO course;
    @ColumnInfo(name = "unread_count")
    private int unreadAnnouncements;

    public CourseDTO getCourse() {
        return course;
    }

    public int getUnreadAnnouncements() {
        return unreadAnnouncements;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public void setUnreadAnnouncements(int unreadAnnouncements) {
        this.unreadAnnouncements = unreadAnnouncements;
    }
}