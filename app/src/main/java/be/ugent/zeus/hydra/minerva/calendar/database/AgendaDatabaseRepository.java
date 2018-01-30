package be.ugent.zeus.hydra.minerva.calendar.database;

import be.ugent.zeus.hydra.minerva.course.database.CourseMapper;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItem;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItemRepository;
import be.ugent.zeus.hydra.common.ExtendedSparseArray;
import org.threeten.bp.OffsetDateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static be.ugent.zeus.hydra.utils.IterableUtils.transform;

/**
 * @author Niko Strijbol
 */
public class AgendaDatabaseRepository implements AgendaItemRepository {

    private final AgendaDao agendaDao;
    private final CourseMapper courseMapper;
    private final AgendaMapper agendaMapper;

    public AgendaDatabaseRepository(AgendaDao agendaDao, CourseMapper courseMapper, AgendaMapper agendaMapper) {
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
    public List<AgendaItem> getAllForCourseFuture(String courseId, OffsetDateTime now) {
        // TODO: make this more performant by not get a separate Course instance per item.
        return transform(agendaDao.getAllFutureForCourse(courseId, now), result -> agendaMapper.convert(result.agendaItem, courseMapper.courseToCourse(result.course)));
    }

    @Override
    public List<AgendaItem> getBetween(OffsetDateTime lower, OffsetDateTime higher) {
        return transform(agendaDao.getBetween(lower, higher), result -> agendaMapper.convert(result.agendaItem, courseMapper.courseToCourse(result.course)));
    }

    @Override
    public ExtendedSparseArray<Long> getIdsAndCalendarIds() {
        ExtendedSparseArray<Long> sparseArray = new ExtendedSparseArray<>();
        for (AgendaDao.IdAndCalendarId item: agendaDao.getIdsAndCalendarIds()) {
            sparseArray.put(item.itemId, item.calendarId);
        }
        return sparseArray;
    }

    @Override
    public List<Long> getCalendarIdsForIds(Collection<Integer> agendaIds) {
        return agendaDao.getCalendarIdsForIds(new ArrayList<>(agendaIds));
    }
}