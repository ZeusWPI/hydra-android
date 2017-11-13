package be.ugent.zeus.hydra.data.database.minerva2.agenda;

import be.ugent.zeus.hydra.data.dto.minerva.AgendaMapper;
import be.ugent.zeus.hydra.data.dto.minerva.CourseMapper;
import be.ugent.zeus.hydra.domain.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.domain.repository.AgendaItemRepository;
import org.threeten.bp.ZonedDateTime;

import java.util.*;

import static be.ugent.zeus.hydra.utils.IterableUtils.transform;

/**
 * @author Niko Strijbol
 */
public class DatabaseAgendaItemRepository implements AgendaItemRepository {

    private final AgendaDao agendaDao;
    private final CourseMapper courseMapper;
    private final AgendaMapper agendaMapper;

    public DatabaseAgendaItemRepository(AgendaDao agendaDao, CourseMapper courseMapper, AgendaMapper agendaMapper) {
        this.agendaDao = agendaDao;
        this.courseMapper = courseMapper;
        this.agendaMapper = agendaMapper;
    }

    @Override
    public AgendaItem getOne(Integer integer) {
        AgendaDao.Result result = agendaDao.getOne(integer);
        return agendaMapper.convert(result.agendaItem, courseMapper.courseToCourse(result.course));
    }

    @Override
    public List<AgendaItem> getAll() {
        return transform(agendaDao.getAll(), result -> agendaMapper.convert(result.agendaItem, courseMapper.courseToCourse(result.course)));
    }

    @Override
    public void insert(AgendaItem object) {
        agendaDao.insert(agendaMapper.convert(object));
    }

    @Override
    public void insert(Collection<AgendaItem> objects) {
        agendaDao.insert(transform(objects, agendaMapper::convert));
    }

    @Override
    public void update(AgendaItem object) {
        agendaDao.update(agendaMapper.convert(object));
    }

    @Override
    public void update(Collection<AgendaItem> objects) {
        agendaDao.update(transform(objects, agendaMapper::convert));
    }

    @Override
    public void delete(AgendaItem object) {
        agendaDao.delete(agendaMapper.convert(object));
    }

    @Override
    public void deleteById(Integer integer) {
        agendaDao.delete(integer);
    }

    @Override
    public void deleteById(Collection<Integer> id) {
        agendaDao.deleteById(new ArrayList<>(id));
    }

    @Override
    public void deleteAll() {
        agendaDao.deleteAll();
    }

    @Override
    public void delete(Collection<AgendaItem> objects) {
        agendaDao.delete(transform(objects, agendaMapper::convert));
    }

    @Override
    public List<AgendaItem> getAllForCourseFuture(String courseId) {
        // TODO: make this more performant by not get a separate Course instance per item.
        ZonedDateTime now = ZonedDateTime.now();
        return transform(agendaDao.getAllFutureForCourse(courseId, now), result -> agendaMapper.convert(result.agendaItem, courseMapper.courseToCourse(result.course)));
    }

    @Override
    public List<AgendaItem> getBetween(ZonedDateTime lower, ZonedDateTime higher) {
        return transform(agendaDao.getBetween(lower, higher), result -> agendaMapper.convert(result.agendaItem, courseMapper.courseToCourse(result.course)));
    }

    @Override
    public Map<AgendaItem, Long> getAllWithCalendarId() {
        List<AgendaDao.Result> items = agendaDao.getAll();
        Map<AgendaItem, Long> map = new HashMap<>();
        for (AgendaDao.Result result : items) {

            map.put(agendaMapper.convert(result.agendaItem, courseMapper.courseToCourse(result.course)), result.agendaItem.getCalendarId());

        }

        return map;
    }

    @Override
    public List<Long> getCalendarIdsForIds(Collection<Integer> agendaIds) {
        return agendaDao.getCalendarIdsForIds(new ArrayList<>(agendaIds));
    }
}