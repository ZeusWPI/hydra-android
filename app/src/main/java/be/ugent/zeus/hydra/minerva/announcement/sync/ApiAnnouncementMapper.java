package be.ugent.zeus.hydra.minerva.announcement.sync;

import be.ugent.zeus.hydra.minerva.announcement.Announcement;
import be.ugent.zeus.hydra.minerva.course.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author Niko Strijbol
 */
@Mapper
public interface ApiAnnouncementMapper {

    ApiAnnouncementMapper INSTANCE = Mappers.getMapper(ApiAnnouncementMapper.class);

    @Mappings({
            @Mapping(source = "courseInstance", target = "course"),
            @Mapping(source = "item.id", target = "itemId"),
            @Mapping(target = "read", ignore = true),
            @Mapping(source = "item.title", target = "title"),
            @Mapping(source = "item.wasEmailSent", target = "emailSent"),
            @Mapping(source = "item.lastEditedAt", target = "date"),
    })
    Announcement convert(ApiAnnouncement item, Course courseInstance);
}