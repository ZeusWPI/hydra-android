package be.ugent.zeus.hydra.minerva.dto;

import be.ugent.zeus.hydra.minerva.Announcement;
import be.ugent.zeus.hydra.minerva.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author Niko Strijbol
 */
@Mapper
public interface AnnouncementMapper {

    AnnouncementMapper INSTANCE = Mappers.getMapper(AnnouncementMapper.class);

    @Mappings({
            @Mapping(source = "read", target = "readAt"),
            @Mapping(source = "emailSent", target = "wasEmailSent"),
            @Mapping(source = "date", target = "lastEditedAt"),
            @Mapping(source = "itemId", target = "id"),
            @Mapping(source = "course.id", target = "courseId")
    })
    AnnouncementDTO convert(Announcement item);

    @Mappings({
            @Mapping(source = "courseInstance", target = "course"),
            @Mapping(source = "item.id", target = "itemId"),
            @Mapping(source = "item.readAt", target = "read"),
            @Mapping(source = "item.title", target = "title"),
            @Mapping(source = "item.wasEmailSent", target = "emailSent"),
            @Mapping(source = "item.lastEditedAt", target = "date"),
    })
    Announcement convert(AnnouncementDTO item, Course courseInstance);
}