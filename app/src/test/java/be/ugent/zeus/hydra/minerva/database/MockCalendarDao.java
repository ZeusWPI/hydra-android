package be.ugent.zeus.hydra.minerva.database;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.minerva.dto.AgendaItemDTO;
import be.ugent.zeus.hydra.minerva.dto.CourseDTO;
import org.threeten.bp.OffsetDateTime;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mock for the calendar dao.
 *
 * Does currently not support transaction-like behaviour of SQL, e.g. when updating an entity for which a foreign key
 * constraint fails, the item will be removed from the set and then the operation will fail.
 *
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MockCalendarDao implements AgendaDao {

    private final List<AgendaItemDTO> calendarItems;
    private final Map<Integer, AgendaItemDTO> idMap;
    private final Map<String, CourseDTO> courses;

    @SuppressLint("UseSparseArrays")
    public MockCalendarDao(List<AgendaItemDTO> calendarItems, List<CourseDTO> courses) {
        this.calendarItems = new ArrayList<>(calendarItems);
        this.idMap = new HashMap<>();
        for (AgendaItemDTO item: calendarItems) {
            idMap.put(item.getId(), item);
        }
        this.courses = new HashMap<>();
        for (CourseDTO course: courses) {
            this.courses.put(course.getId(), course);
        }
    }

    @Override
    public Result getOne(int id) {
        if (!idMap.containsKey(id)) {
            return null;
        }
        Result result = new Result();
        result.agendaItem = idMap.get(id);
        result.course = courses.get(result.agendaItem.getCourseId());
        return result;
    }

    @Override
    public List<Result> getAll() {
        return this.calendarItems.stream()
                .map(a -> {
                    Result result = new Result();
                    result.agendaItem = a;
                    result.course = courses.get(a.getCourseId());
                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void insert(AgendaItemDTO item) {
        if (!courses.containsKey(item.getCourseId())) {
            throw new SQLiteConstraintException("Foreign key failed.");
        }
        calendarItems.add(item);
        idMap.put(item.getId(), item);
    }

    @Override
    public void insert(Collection<AgendaItemDTO> items) {
        items.forEach(this::insert);
    }

    @Override
    public void update(AgendaItemDTO item) {
        delete(item.getId());
        insert(item);
    }

    @Override
    public void update(Collection<AgendaItemDTO> items) {
        items.forEach(this::update);
    }

    @Override
    public void delete(AgendaItemDTO item) {
        this.calendarItems.remove(item);
        this.idMap.remove(item.getId());
    }

    @Override
    public void delete(Collection<AgendaItemDTO> items) {
        items.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        this.calendarItems.clear();
        this.idMap.clear();
    }

    @Override
    public void deleteById(List<Integer> ids) {
        ids.forEach(this::delete);
    }

    @Override
    public void delete(int id) {
        this.calendarItems.remove(this.idMap.get(id));
        this.idMap.remove(id);
    }

    @Override
    public List<Result> getAllFutureForCourse(String courseId, OffsetDateTime now) {
        return this.calendarItems.stream()
                .filter(i -> i.getCourseId().equals(courseId))
                .filter(i -> i.getEndDate().isAfter(now) || i.getEndDate().isEqual(now))
                .map((Function<AgendaItemDTO, Result>) c -> {
                    Result result = new Result();
                    result.agendaItem = c;
                    result.course = courses.get(c.getCourseId());
                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Result> getAllForCourse(String courseId) {
        return calendarItems.stream()
                .filter(i -> i.getCourseId().equals(courseId))
                .map((Function<AgendaItemDTO, Result>) c -> {
                    Result result = new Result();
                    result.agendaItem = c;
                    result.course = courses.get(c.getCourseId());
                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Result> getBetween(OffsetDateTime lower, OffsetDateTime upper) {
        return calendarItems.stream()
                .filter(c -> {
                    boolean startAfter = c.getStartDate().isAfter(lower) || c.getStartDate().isEqual(lower);
                    boolean endAfter = c.getEndDate().isAfter(lower) || c.getEndDate().isEqual(lower);
                    boolean startUpper = c.getStartDate().isBefore(upper) || c.getStartDate().isEqual(upper);
                    return (startAfter || endAfter) && startUpper;
                })
                .map((Function<AgendaItemDTO, Result>) c -> {
                    Result result = new Result();
                    result.agendaItem = c;
                    result.course = courses.get(c.getCourseId());
                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getCalendarIdsForIds(List<Integer> ids) {
        Set<Integer> idSet = new HashSet<>(ids);
        return calendarItems.stream()
                .filter(i -> idSet.contains(i.getId()))
                .map(AgendaItemDTO::getCalendarId)
                .collect(Collectors.toList());
    }

    @Override
    public List<IdAndCalendarId> getIdsAndCalendarIds() {
        return calendarItems.stream()
                .map(agendaItemDTO -> {
                    IdAndCalendarId idAndCalendarId = new IdAndCalendarId();
                    idAndCalendarId.calendarId = agendaItemDTO.getCalendarId();
                    idAndCalendarId.itemId = agendaItemDTO.getId();
                    return idAndCalendarId;
                })
                .collect(Collectors.toList());
    }
}