package be.ugent.zeus.hydra.minerva.course.database;

import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.course.Module;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Converts {@link CourseDTO} to {@link Course}.
 *
 * @author Niko Strijbol
 */
@Mapper(uses = Module.class)
public interface CourseMapper {

    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    @Mapping(source = "tutor", target = "tutorName")
    @Mapping(source = "year", target = "academicYear")
    @Mapping(target = "enabledModules", ignore = true)
    Course courseToCourse(CourseDTO course);

    @InheritInverseConfiguration
    CourseDTO courseToCourse(Course course);
}