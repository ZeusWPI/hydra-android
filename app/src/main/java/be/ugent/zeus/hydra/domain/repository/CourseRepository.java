package be.ugent.zeus.hydra.domain.repository;


import android.util.Pair;

import be.ugent.zeus.hydra.domain.models.minerva.Course;

import java.util.List;
import java.util.Map;

/**
 * @author Niko Strijbol
 */
public interface CourseRepository extends FullRepository<String, Course> {

    List<Course> getIn(List<String> ids);

    List<Pair<Course, Integer>> getAllAndUnreadInOrder();

    Map<String, Integer> getIdToOrderMap();

    List<String> getIds();
}