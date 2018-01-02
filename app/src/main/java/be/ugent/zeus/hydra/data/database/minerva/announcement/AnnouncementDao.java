package be.ugent.zeus.hydra.data.database.minerva.announcement;

import android.arch.persistence.room.*;

import be.ugent.zeus.hydra.data.database.minerva.course.CourseTable;
import be.ugent.zeus.hydra.data.dto.minerva.AnnouncementDTO;
import be.ugent.zeus.hydra.data.dto.minerva.CourseDTO;
import org.threeten.bp.Instant;

import java.util.Collection;
import java.util.List;

/**
 * @author Niko Strijbol
 */
@Dao
public interface AnnouncementDao {

    @Query("SELECT a.*, c." + CourseTable.Columns.ID + " AS c_" + CourseTable.Columns.ID +
            ", c." + CourseTable.Columns.CODE + " AS c_" + CourseTable.Columns.CODE +
            ", c." + CourseTable.Columns.TITLE + " AS c_" + CourseTable.Columns.TITLE +
            ", c." + CourseTable.Columns.DESCRIPTION + " AS c_" + CourseTable.Columns.DESCRIPTION +
            ", c." + CourseTable.Columns.TUTOR + " AS c_" + CourseTable.Columns.TUTOR +
            ", c." + CourseTable.Columns.ACADEMIC_YEAR + " AS c_" + CourseTable.Columns.ACADEMIC_YEAR +
            ", c." + CourseTable.Columns.ORDER + " AS c_" + CourseTable.Columns.ORDER +
            " FROM " + AnnouncementTable.TABLE_NAME + " a LEFT JOIN " + CourseTable.TABLE_NAME + " c ON a." + AnnouncementTable.Columns.COURSE + " = c." + CourseTable.Columns.ID + " WHERE a." + AnnouncementTable.Columns.ID + " IS :id")
    Result getOne(int id);

    @Query("SELECT a.*, c." + CourseTable.Columns.ID + " AS c_" + CourseTable.Columns.ID +
            ", c." + CourseTable.Columns.CODE + " AS c_" + CourseTable.Columns.CODE +
            ", c." + CourseTable.Columns.TITLE + " AS c_" + CourseTable.Columns.TITLE +
            ", c." + CourseTable.Columns.DESCRIPTION + " AS c_" + CourseTable.Columns.DESCRIPTION +
            ", c." + CourseTable.Columns.TUTOR + " AS c_" + CourseTable.Columns.TUTOR +
            ", c." + CourseTable.Columns.ACADEMIC_YEAR + " AS c_" + CourseTable.Columns.ACADEMIC_YEAR +
            ", c." + CourseTable.Columns.ORDER + " AS c_" + CourseTable.Columns.ORDER +
            " FROM " + AnnouncementTable.TABLE_NAME + " a LEFT JOIN " + CourseTable.TABLE_NAME + " c ON a." + AnnouncementTable.Columns.COURSE + " = c." + CourseTable.Columns.ID)
    List<Result> getAll();

    @Insert
    void insert(AnnouncementDTO announcement);

    @Insert
    void insert(Collection<AnnouncementDTO> announcements);

    @Update
    void update(AnnouncementDTO announcement);

    @Update
    void update(Collection<AnnouncementDTO> announcements);

    @Delete
    void delete(AnnouncementDTO announcement);

    @Delete
    void delete(Collection<AnnouncementDTO> announcements);

    @Query("DELETE FROM " + AnnouncementTable.TABLE_NAME)
    void deleteAll();

    @Query("DELETE FROM " + AnnouncementTable.TABLE_NAME + " WHERE " + AnnouncementTable.Columns.ID + " IN (:ids)")
    void deleteById(List<Integer> ids);

    @Query("DELETE FROM " + AnnouncementTable.TABLE_NAME + " WHERE " + AnnouncementTable.Columns.ID + " IS :id")
    void delete(int id);

    @Query("SELECT a.*, c." + CourseTable.Columns.ID + " AS c_" + CourseTable.Columns.ID +
            ", c." + CourseTable.Columns.CODE + " AS c_" + CourseTable.Columns.CODE +
            ", c." + CourseTable.Columns.TITLE + " AS c_" + CourseTable.Columns.TITLE +
            ", c." + CourseTable.Columns.DESCRIPTION + " AS c_" + CourseTable.Columns.DESCRIPTION +
            ", c." + CourseTable.Columns.TUTOR + " AS c_" + CourseTable.Columns.TUTOR +
            ", c." + CourseTable.Columns.ACADEMIC_YEAR + " AS c_" + CourseTable.Columns.ACADEMIC_YEAR +
            ", c." + CourseTable.Columns.ORDER + " AS c_" + CourseTable.Columns.ORDER +
            " FROM " + AnnouncementTable.TABLE_NAME + " a LEFT JOIN " + CourseTable.TABLE_NAME + " c ON a." + AnnouncementTable.Columns.COURSE + " = c." + CourseTable.Columns.ID + " WHERE a." + AnnouncementTable.Columns.READ_DATE + " = -1 ORDER BY a." + AnnouncementTable.Columns.DATE + " DESC")
    List<Result> getUnreadMostRecentFirst();

    @Query("SELECT a.*, c." + CourseTable.Columns.ID + " AS c_" + CourseTable.Columns.ID +
            ", c." + CourseTable.Columns.CODE + " AS c_" + CourseTable.Columns.CODE +
            ", c." + CourseTable.Columns.TITLE + " AS c_" + CourseTable.Columns.TITLE +
            ", c." + CourseTable.Columns.DESCRIPTION + " AS c_" + CourseTable.Columns.DESCRIPTION +
            ", c." + CourseTable.Columns.TUTOR + " AS c_" + CourseTable.Columns.TUTOR +
            ", c." + CourseTable.Columns.ACADEMIC_YEAR + " AS c_" + CourseTable.Columns.ACADEMIC_YEAR +
            ", c." + CourseTable.Columns.ORDER + " AS c_" + CourseTable.Columns.ORDER +
            " FROM " + AnnouncementTable.TABLE_NAME + " a LEFT JOIN " + CourseTable.TABLE_NAME + " c ON a." + AnnouncementTable.Columns.COURSE + " = c." + CourseTable.Columns.ID + " WHERE a." + AnnouncementTable.Columns.COURSE + " = :courseId ORDER BY a." + AnnouncementTable.Columns.DATE + " DESC")
    List<Result> getMostRecentFirst(String courseId);

    @Query("SELECT " + AnnouncementTable.Columns.ID + ", " + AnnouncementTable.Columns.READ_DATE + " FROM " + AnnouncementTable.TABLE_NAME + " WHERE " + AnnouncementTable.Columns.COURSE + " = :courseId ORDER BY " + AnnouncementTable.Columns.DATE + " DESC")
    List<IdAndReadDate> getIdAndReadDateFor(String courseId);

    class Result {
        @Embedded
        public AnnouncementDTO announcement;
        @Embedded(prefix = "c_")
        public CourseDTO course;
    }

    class IdAndReadDate {
        @ColumnInfo(name = AnnouncementTable.Columns.ID)
        public int id;
        @ColumnInfo(name = AnnouncementTable.Columns.READ_DATE)
        public Instant readAt;
    }
}
