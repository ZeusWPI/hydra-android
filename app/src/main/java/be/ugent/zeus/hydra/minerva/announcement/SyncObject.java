package be.ugent.zeus.hydra.minerva.announcement;

import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;

import java.util.Collection;

/**
 * Utility class. A DAO can request such an object to determine how to synchronize.
 *
 * TODO: examine if a library like https://github.com/google/FreeBuilder would work better.
 *
 * @author Niko Strijbol
 */
public class SyncObject {

    private Collection<Announcement> allObjects;
    private Collection<Announcement> newObjects;
    private Course course;
    private boolean firstSync;

    private SyncObject(Course course) {
        this.course = course;
    }

    public Collection<Announcement> getAllObjects() {
        return allObjects;
    }

    public Collection<Announcement> getNewObjects() {
        return newObjects;
    }

    public Course getCourse() {
        return course;
    }

    public boolean isFirstSync() {
        return firstSync;
    }

    public static class Builder {

        private final SyncObject object;

        public Builder(Course course) {
            this.object = new SyncObject(course);
        }

        public Builder allObjects(Collection<Announcement> allObjects) {
            object.allObjects = allObjects;
            return this;
        }

        public Builder newObjects(Collection<Announcement> newObjects) {
            object.newObjects = newObjects;
            return this;
        }

        public Builder setFirstSync(boolean isFirst) {
            object.firstSync = isFirst;
            return this;
        }

        public SyncObject build() {
            return object;
        }
    }
}