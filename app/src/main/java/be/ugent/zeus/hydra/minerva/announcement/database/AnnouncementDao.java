package be.ugent.zeus.hydra.minerva.announcement.database;

import androidx.room.*;

import be.ugent.zeus.hydra.minerva.provider.CourseContract;
import be.ugent.zeus.hydra.minerva.course.database.CourseDTO;
import org.threeten.bp.Instant;

import java.util.Collection;
import java.util.List;

/**
 * @author Niko Strijbol
 */
@Dao
public interface AnnouncementDao {

    @Query("SELECT a.*, c." + CourseContract.Columns.ID + " AS c_" + CourseContract.Columns.ID +
            ", c." + CourseContract.Columns.CODE + " AS c_" + CourseContract.Columns.CODE +
            ", c." + CourseContract.Columns.TITLE + " AS c_" + CourseContract.Columns.TITLE +
            ", c." + CourseContract.Columns.DESCRIPTION + " AS c_" + CourseContract.Columns.DESCRIPTION +
            ", c." + CourseContract.Columns.TUTOR + " AS c_" + CourseContract.Columns.TUTOR +
            ", c." + CourseContract.Columns.ACADEMIC_YEAR + " AS c_" + CourseContract.Columns.ACADEMIC_YEAR +
            ", c." + CourseContract.Columns.ORDER + " AS c_" + CourseContract.Columns.ORDER +
            ", c." + CourseContract.Columns.DISABLED_MODULES + " AS c_" + CourseContract.Columns.DISABLED_MODULES +
            ", c." + CourseContract.Columns.IGNORE_ANNOUNCEMENTS + " AS c_" + CourseContract.Columns.IGNORE_ANNOUNCEMENTS +
            ", c." + CourseContract.Columns.IGNORE_CALENDAR + " AS c_" + CourseContract.Columns.IGNORE_CALENDAR +
            " FROM " + AnnouncementTable.TABLE_NAME + " a LEFT JOIN " + CourseContract.TABLE_NAME + " c ON a." + AnnouncementTable.Columns.COURSE + " = c." + CourseContract.Columns.ID + " WHERE a." + AnnouncementTable.Columns.ID + " IS :id")
    Result getOne(int id);

    @Query("SELECT a.*, c." + CourseContract.Columns.ID + " AS c_" + CourseContract.Columns.ID +
            ", c." + CourseContract.Columns.CODE + " AS c_" + CourseContract.Columns.CODE +
            ", c." + CourseContract.Columns.TITLE + " AS c_" + CourseContract.Columns.TITLE +
            ", c." + CourseContract.Columns.DESCRIPTION + " AS c_" + CourseContract.Columns.DESCRIPTION +
            ", c." + CourseContract.Columns.TUTOR + " AS c_" + CourseContract.Columns.TUTOR +
            ", c." + CourseContract.Columns.ACADEMIC_YEAR + " AS c_" + CourseContract.Columns.ACADEMIC_YEAR +
            ", c." + CourseContract.Columns.ORDER + " AS c_" + CourseContract.Columns.ORDER +
            ", c." + CourseContract.Columns.DISABLED_MODULES + " AS c_" + CourseContract.Columns.DISABLED_MODULES +
            ", c." + CourseContract.Columns.IGNORE_ANNOUNCEMENTS + " AS c_" + CourseContract.Columns.IGNORE_ANNOUNCEMENTS +
            ", c." + CourseContract.Columns.IGNORE_CALENDAR + " AS c_" + CourseContract.Columns.IGNORE_CALENDAR +
            " FROM " + AnnouncementTable.TABLE_NAME + " a LEFT JOIN " + CourseContract.TABLE_NAME + " c ON a." + AnnouncementTable.Columns.COURSE + " = c." + CourseContract.Columns.ID)
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

    @Query("SELECT a.*, c." + CourseContract.Columns.ID + " AS c_" + CourseContract.Columns.ID +
            ", c." + CourseContract.Columns.CODE + " AS c_" + CourseContract.Columns.CODE +
            ", c." + CourseContract.Columns.TITLE + " AS c_" + CourseContract.Columns.TITLE +
            ", c." + CourseContract.Columns.DESCRIPTION + " AS c_" + CourseContract.Columns.DESCRIPTION +
            ", c." + CourseContract.Columns.TUTOR + " AS c_" + CourseContract.Columns.TUTOR +
            ", c." + CourseContract.Columns.ACADEMIC_YEAR + " AS c_" + CourseContract.Columns.ACADEMIC_YEAR +
            ", c." + CourseContract.Columns.ORDER + " AS c_" + CourseContract.Columns.ORDER +
            ", c." + CourseContract.Columns.DISABLED_MODULES + " AS c_" + CourseContract.Columns.DISABLED_MODULES +
            ", c." + CourseContract.Columns.IGNORE_ANNOUNCEMENTS + " AS c_" + CourseContract.Columns.IGNORE_ANNOUNCEMENTS +
            ", c." + CourseContract.Columns.IGNORE_CALENDAR + " AS c_" + CourseContract.Columns.IGNORE_CALENDAR +
            " FROM " + AnnouncementTable.TABLE_NAME + " a LEFT JOIN " + CourseContract.TABLE_NAME + " c ON a." + AnnouncementTable.Columns.COURSE + " = c." + CourseContract.Columns.ID +
            " WHERE a." + AnnouncementTable.Columns.READ_DATE + " ISNULL ORDER BY datetime(a." + AnnouncementTable.Columns.DATE + ") DESC")
    List<Result> getUnreadMostRecentFirst();

    @Query("SELECT a.*, c." + CourseContract.Columns.ID + " AS c_" + CourseContract.Columns.ID +
            ", c." + CourseContract.Columns.CODE + " AS c_" + CourseContract.Columns.CODE +
            ", c." + CourseContract.Columns.TITLE + " AS c_" + CourseContract.Columns.TITLE +
            ", c." + CourseContract.Columns.DESCRIPTION + " AS c_" + CourseContract.Columns.DESCRIPTION +
            ", c." + CourseContract.Columns.TUTOR + " AS c_" + CourseContract.Columns.TUTOR +
            ", c." + CourseContract.Columns.ACADEMIC_YEAR + " AS c_" + CourseContract.Columns.ACADEMIC_YEAR +
            ", c." + CourseContract.Columns.ORDER + " AS c_" + CourseContract.Columns.ORDER +
            ", c." + CourseContract.Columns.DISABLED_MODULES + " AS c_" + CourseContract.Columns.DISABLED_MODULES +
            ", c." + CourseContract.Columns.IGNORE_ANNOUNCEMENTS + " AS c_" + CourseContract.Columns.IGNORE_ANNOUNCEMENTS +
            ", c." + CourseContract.Columns.IGNORE_CALENDAR + " AS c_" + CourseContract.Columns.IGNORE_CALENDAR +
            " FROM " + AnnouncementTable.TABLE_NAME + " a LEFT JOIN " + CourseContract.TABLE_NAME + " c ON a." + AnnouncementTable.Columns.COURSE + " = c." + CourseContract.Columns.ID +
            " WHERE a." + AnnouncementTable.Columns.COURSE + " = :courseId ORDER BY datetime(a." + AnnouncementTable.Columns.DATE + ") DESC")
    List<Result> getMostRecentFirst(String courseId);

    @Query("SELECT " + AnnouncementTable.Columns.ID + ", " + AnnouncementTable.Columns.READ_DATE + " FROM " + AnnouncementTable.TABLE_NAME + " WHERE " + AnnouncementTable.Columns.COURSE + " = :courseId ORDER BY datetime(" + AnnouncementTable.Columns.DATE + ") DESC")
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
