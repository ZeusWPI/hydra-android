package be.ugent.zeus.hydra.minerva.calendar.database;

import androidx.room.*;

import be.ugent.zeus.hydra.minerva.provider.CourseContract;
import be.ugent.zeus.hydra.minerva.course.database.CourseDTO;
import org.threeten.bp.OffsetDateTime;

import java.util.Collection;
import java.util.List;

/**
 * @author Niko Strijbol
 */
@Dao
public interface AgendaDao {

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
            " FROM " + AgendaTable.TABLE_NAME + " a LEFT JOIN " + CourseContract.TABLE_NAME + " c ON a." + AgendaTable.Columns.COURSE + " = c." + CourseContract.Columns.ID + " WHERE a." + AgendaTable.Columns.ID + " IS :id")
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
            " FROM " + AgendaTable.TABLE_NAME + " a LEFT JOIN " + CourseContract.TABLE_NAME + " c ON a." + AgendaTable.Columns.COURSE + " = c." + CourseContract.Columns.ID)
    List<Result> getAll();

    @Insert
    void insert(AgendaItemDTO item);

    @Insert
    void insert(Collection<AgendaItemDTO> items);

    @Update
    void update(AgendaItemDTO item);

    @Update
    void update(Collection<AgendaItemDTO> items);

    @Delete
    void delete(AgendaItemDTO item);

    @Delete
    void delete(Collection<AgendaItemDTO> items);

    @Query("DELETE FROM " + AgendaTable.TABLE_NAME)
    void deleteAll();

    @Query("DELETE FROM " + AgendaTable.TABLE_NAME + " WHERE " + AgendaTable.Columns.ID + " IN (:ids)")
    void deleteById(List<Integer> ids);

    @Query("DELETE FROM " + AgendaTable.TABLE_NAME + " WHERE " + AgendaTable.Columns.ID + " IS :id")
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
            " FROM " + AgendaTable.TABLE_NAME + " a LEFT JOIN " + CourseContract.TABLE_NAME + " c ON a." + AgendaTable.Columns.COURSE + " = c." + CourseContract.Columns.ID +
            " WHERE a." + AgendaTable.Columns.COURSE + " = :courseId" +
            " AND datetime(a." + AgendaTable.Columns.END_DATE + ") >= datetime(:now)" +
            " ORDER BY datetime(" + AgendaTable.Columns.START_DATE + ") ASC"
    )
    List<Result> getAllFutureForCourse(String courseId, OffsetDateTime now);

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
            " FROM " + AgendaTable.TABLE_NAME + " a LEFT JOIN " + CourseContract.TABLE_NAME + " c ON a." + AgendaTable.Columns.COURSE + " = c." + CourseContract.Columns.ID +
            " WHERE a." + AgendaTable.Columns.COURSE + " = :courseId" +
            " ORDER BY datetime(" + AgendaTable.Columns.START_DATE + ") ASC"
    )
    List<Result> getAllForCourse(String courseId);

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
            " FROM " + AgendaTable.TABLE_NAME + " a LEFT JOIN " + CourseContract.TABLE_NAME + " c ON a." + AgendaTable.Columns.COURSE + " = c." + CourseContract.Columns.ID +
            " WHERE ((datetime(a." + AgendaTable.Columns.START_DATE + ") >= datetime(:lower) OR datetime(a." + AgendaTable.Columns.END_DATE + ") >= datetime(:lower))" +
            " AND datetime(a." + AgendaTable.Columns.START_DATE + ") <= datetime(:upper)) " +
            " AND c_" + CourseContract.Columns.IGNORE_CALENDAR + " = 0" +
            " ORDER BY datetime(a." + AgendaTable.Columns.START_DATE + ") ASC"
    )
    List<Result> getBetweenNonIgnored(OffsetDateTime lower, OffsetDateTime upper);

    @Query("SELECT " + AgendaTable.Columns.CALENDAR_ID + " FROM " + AgendaTable.TABLE_NAME + " WHERE " + AgendaTable.Columns.ID + " IN (:ids)")
    List<Long> getCalendarIdsForIds(List<Integer> ids);

    @Query("SELECT " + AgendaTable.Columns.ID + ", " + AgendaTable.Columns.CALENDAR_ID + " FROM " + AgendaTable.TABLE_NAME)
    List<IdAndCalendarId> getIdsAndCalendarIds();

    class Result {
        @Embedded
        public AgendaItemDTO agendaItem;
        @Embedded(prefix = "c_")
        public CourseDTO course;
    }

    class IdAndCalendarId {
        @ColumnInfo(name = AgendaTable.Columns.ID)
        public int itemId;
        @ColumnInfo(name = AgendaTable.Columns.CALENDAR_ID)
        public Long calendarId;
    }
}
