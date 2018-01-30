package be.ugent.zeus.hydra.minerva.calendar.sync;

import be.ugent.zeus.hydra.minerva.calendar.AgendaItem;
import be.ugent.zeus.hydra.minerva.course.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * Maps API objects to general objects.
 *
 * @author Niko Strijbol
 */
@Mapper
interface ApiCalendarMapper {

    ApiCalendarMapper INSTANCE = Mappers.getMapper(ApiCalendarMapper.class);

    @Mappings({
            @Mapping(source = "courseInstance", target = "course"),
            @Mapping(source = "item.id", target = "itemId"),
            @Mapping(source = "item.title", target = "title"),
            @Mapping(source = "item.lastEditDate", target = "lastEdited"),
            @Mapping(target = "merged", ignore = true),
            @Mapping(target = "calendarId", ignore = true)
    })
    AgendaItem convert(ApiCalendarItem item, Course courseInstance);
}