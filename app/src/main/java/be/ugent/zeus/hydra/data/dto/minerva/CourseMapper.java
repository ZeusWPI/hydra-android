package be.ugent.zeus.hydra.data.dto.minerva;

import be.ugent.zeus.hydra.domain.models.minerva.Course;
import be.ugent.zeus.hydra.domain.models.minerva.Module;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * Converts {@link CourseDTO} to {@link Course}.
 *
 * @author Niko Strijbol
 */
@Mapper(uses = Module.class)
public interface CourseMapper {

    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    @Mappings({
            @Mapping(source = "tutor", target = "tutorName"),
            @Mapping(source = "year", target = "academicYear")
    })
    Course courseToCourse(CourseDTO course);

    @InheritInverseConfiguration
    CourseDTO courseToCourse(Course course);
}