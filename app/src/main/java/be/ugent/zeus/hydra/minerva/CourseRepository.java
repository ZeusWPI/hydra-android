package be.ugent.zeus.hydra.minerva;

import android.util.Pair;

import be.ugent.zeus.hydra.common.FullRepository;
import java8.util.Objects;

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

        public LocalData(int order, EnumSet<Module> disabledModules) {
            this.order = order;
            this.disabledModules = disabledModules;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocalData localData = (LocalData) o;
            return order == localData.order &&
                    Objects.equals(disabledModules, localData.disabledModules);
        }

        @Override
        public int hashCode() {
            return Objects.hash(order, disabledModules);
        }
    }
}