package be.ugent.zeus.hydra.minerva.course.sync;

import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.course.database.CourseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * Converts {@link CourseDTO} to {@link Course}.
 *
 * @author Niko Strijbol
 */
@Mapper
interface ApiCourseMapper {

    ApiCourseMapper INSTANCE = Mappers.getMapper(ApiCourseMapper.class);

    @Mappings({
            @Mapping(source = "tutor", target = "tutorName"),
            @Mapping(source = "year", target = "academicYear"),
            @Mapping(target = "order", ignore = true),
            @Mapping(target = "disabledModules", ignore = true)
    })
    Course courseToCourse(ApiCourse course);
}