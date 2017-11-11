package be.ugent.zeus.hydra.data.dto.minerva;

import be.ugent.zeus.hydra.domain.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.domain.models.minerva.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author Niko Strijbol
 */
@Mapper
public interface AgendaMapper {

    AgendaMapper INSTANCE = Mappers.getMapper(AgendaMapper.class);

    @Mappings({
            @Mapping(source = "course.id", target = "courseId"),
            @Mapping(source = "itemId", target = "id"),
            @Mapping(source = "lastEdited", target = "lastEditDate")
    })
    AgendaItemDTO convert(AgendaItem item);

    @Mappings({
            @Mapping(source = "courseInstance", target = "course"),
            @Mapping(source = "item.id", target = "itemId"),
            @Mapping(source = "item.title", target = "title"),
            @Mapping(source = "item.lastEditDate", target = "lastEdited")
    })
    AgendaItem convert(AgendaItemDTO item, Course courseInstance);
}