package be.ugent.zeus.hydra.minerva.course;

import android.util.Pair;

import be.ugent.zeus.hydra.common.FullRepository;
import java9.util.Objects;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Strijbol
 */
public interface CourseRepository extends FullRepository<String, Course> {

    List<Course> getIn(List<String> ids);

    List<Pair<Course, Long>> getAllAndUnreadInOrder();

    /**
     * Get a mapping from the course ID to the local data.
     *
     * @return The map.
     */
    Map<String, LocalData> getIdToLocalData();

    /**
     * Get a list of all IDs.
     *
     * @return The list of IDs.
     */
    List<String> getIds();

    class LocalData {

        public final int order;
        public final EnumSet<Module> disabledModules;
        public final boolean ignoreAnnouncements;
        public final boolean ignoreCalendar;

        public LocalData(int order, EnumSet<Module> disabledModules, boolean ignoreAnnouncements, boolean ignoreCalendar) {
            this.order = order;
            this.disabledModules = disabledModules;
            this.ignoreAnnouncements = ignoreAnnouncements;
            this.ignoreCalendar = ignoreCalendar;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocalData localData = (LocalData) o;
            return order == localData.order &&
                    ignoreAnnouncements == localData.ignoreAnnouncements &&
                    ignoreCalendar == localData.ignoreCalendar &&
                    Objects.equals(disabledModules, localData.disabledModules);
        }

        @Override
        public int hashCode() {
            return Objects.hash(order, disabledModules, ignoreAnnouncements, ignoreCalendar);
        }
    }
}