package be.ugent.zeus.hydra.minerva.announcement;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementDao;
import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementDTO;
import be.ugent.zeus.hydra.minerva.course.database.CourseDTO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mock for the announcement dao.
 *
 * Does currently not support transaction-like behaviour of SQL, e.g. when updating an entity for which a foreign key
 * constraint fails, the item will be removed from the set and then the operation will fail.
 *
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MockAnnouncementDao implements AnnouncementDao {

    private final List<AnnouncementDTO> announcements;
    private final Map<Integer, AnnouncementDTO> idMap;
    private final Map<String, CourseDTO> courses;

    @SuppressLint("UseSparseArrays")
    MockAnnouncementDao(List<AnnouncementDTO> announcements, List<CourseDTO> courses) {
        this.announcements = new ArrayList<>(announcements);
        this.idMap = new HashMap<>();
        for (AnnouncementDTO a: announcements) {
            idMap.put(a.getId(), a);
        }
        this.courses = new HashMap<>();
        for (CourseDTO c : courses) {
            this.courses.put(c.getId(), c);
        }
    }

    @Nullable
    @Override
    public Result getOne(int id) {
        if (!idMap.containsKey(id)) {
            return null;
        }
        Result result = new Result();
        result.announcement = idMap.get(id);
        result.course = courses.get(Objects.requireNonNull(result.announcement).getCourseId());
        return result;
    }

    @Override
    public List<Result> getAll() {
        return this.announcements.stream()
                .map(a -> {
                    Result result = new Result();
                    result.announcement = a;
                    result.course = courses.get(a.getCourseId());
                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void insert(AnnouncementDTO announcement) {
        if (!courses.containsKey(announcement.getCourseId())) {
            throw new SQLiteConstraintException("Foreign key failed.");
        }
        announcements.add(announcement);
        idMap.put(announcement.getId(), announcement);
    }

    @Override
    public void insert(Collection<AnnouncementDTO> announcements) {
        announcements.forEach(this::insert);
    }

    @Override
    public void update(AnnouncementDTO announcement) {
        delete(announcement.getId());
        insert(announcement);
    }

    @Override
    public void update(Collection<AnnouncementDTO> announcements) {
        announcements.forEach(this::update);
    }

    @Override
    public void delete(AnnouncementDTO announcement) {
        announcements.remove(announcement);
        idMap.remove(announcement.getId());
    }

    @Override
    public void delete(Collection<AnnouncementDTO> announcements) {
        announcements.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        announcements.clear();
        idMap.clear();
    }

    @Override
    public void deleteById(List<Integer> ids) {
        ids.forEach(this::delete);
    }

    @Override
    public void delete(int id) {
        announcements.remove(idMap.get(id));
        idMap.remove(id);
    }

    @Override
    public List<Result> getUnreadMostRecentFirst() {
        return this.announcements.stream()
                .filter(a -> a.getReadAt() == null)
                .sorted(Comparator.comparing(AnnouncementDTO::getLastEditedAt).reversed())
                .map(announcementDTO -> {
                    Result result = new Result();
                    result.announcement = announcementDTO;
                    result.course = courses.get(announcementDTO.getCourseId());
                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Result> getMostRecentFirst(String courseId) {
        return this.announcements.stream()
                .filter(a -> a.getCourseId().equals(courseId))
                .sorted(Comparator.comparing(AnnouncementDTO::getLastEditedAt).reversed())
                .map(announcementDTO -> {
                    Result result = new Result();
                    result.announcement = announcementDTO;
                    result.course = courses.get(announcementDTO.getCourseId());
                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<IdAndReadDate> getIdAndReadDateFor(String courseId) {
        return this.announcements.stream()
                .filter(a -> a.getCourseId().equals(courseId))
                .sorted(Comparator.comparing(AnnouncementDTO::getLastEditedAt).reversed())
                .map(announcementDTO -> {
                    IdAndReadDate idAndReadDate = new IdAndReadDate();
                    idAndReadDate.id = announcementDTO.getId();
                    idAndReadDate.readAt = announcementDTO.getReadAt();
                    return idAndReadDate;
                })
                .collect(Collectors.toList());
    }
}