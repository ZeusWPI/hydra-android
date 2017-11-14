package be.ugent.zeus.hydra.data.database.minerva2.agenda;

import android.arch.persistence.room.*;

import be.ugent.zeus.hydra.data.database.minerva2.course.CourseTable;
import be.ugent.zeus.hydra.data.dto.minerva.AgendaItemDTO;
import be.ugent.zeus.hydra.data.dto.minerva.CourseDTO;
import org.threeten.bp.ZonedDateTime;

import java.util.Collection;
import java.util.List;

/**
 * @author Niko Strijbol
 */
@Dao
public interface AgendaDao {

    @Query("SELECT a.*, c." + CourseTable.Columns.ID + " AS c_" + CourseTable.Columns.ID +
            ", c." + CourseTable.Columns.CODE + " AS c_" + CourseTable.Columns.CODE +
            ", c." + CourseTable.Columns.TITLE + " AS c_" + CourseTable.Columns.TITLE +
            ", c." + CourseTable.Columns.DESCRIPTION + " AS c_" + CourseTable.Columns.DESCRIPTION +
            ", c." + CourseTable.Columns.TUTOR + " AS c_" + CourseTable.Columns.TUTOR +
            ", c." + CourseTable.Columns.ACADEMIC_YEAR + " AS c_" + CourseTable.Columns.ACADEMIC_YEAR +
            ", c." + CourseTable.Columns.ORDER + " AS c_" + CourseTable.Columns.ORDER +
            " FROM " + AgendaTable.TABLE_NAME + " a LEFT JOIN " + CourseTable.TABLE_NAME + " c ON a." + AgendaTable.Columns.COURSE + " = c." + CourseTable.Columns.ID + " WHERE a." + AgendaTable.Columns.ID + " IS :id")
    Result getOne(int id);

    @Query("SELECT a.*, c." + CourseTable.Columns.ID + " AS c_" + CourseTable.Columns.ID +
            ", c." + CourseTable.Columns.CODE + " AS c_" + CourseTable.Columns.CODE +
            ", c." + CourseTable.Columns.TITLE + " AS c_" + CourseTable.Columns.TITLE +
            ", c." + CourseTable.Columns.DESCRIPTION + " AS c_" + CourseTable.Columns.DESCRIPTION +
            ", c." + CourseTable.Columns.TUTOR + " AS c_" + CourseTable.Columns.TUTOR +
            ", c." + CourseTable.Columns.ACADEMIC_YEAR + " AS c_" + CourseTable.Columns.ACADEMIC_YEAR +
            ", c." + CourseTable.Columns.ORDER + " AS c_" + CourseTable.Columns.ORDER +
            " FROM " + AgendaTable.TABLE_NAME + " a LEFT JOIN " + CourseTable.TABLE_NAME + " c ON a." + AgendaTable.Columns.COURSE + " = c." + CourseTable.Columns.ID)
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

    @Query("SELECT a.*, c." + CourseTable.Columns.ID + " AS c_" + CourseTable.Columns.ID +
            ", c." + CourseTable.Columns.CODE + " AS c_" + CourseTable.Columns.CODE +
            ", c." + CourseTable.Columns.TITLE + " AS c_" + CourseTable.Columns.TITLE +
            ", c." + CourseTable.Columns.DESCRIPTION + " AS c_" + CourseTable.Columns.DESCRIPTION +
            ", c." + CourseTable.Columns.TUTOR + " AS c_" + CourseTable.Columns.TUTOR +
            ", c." + CourseTable.Columns.ACADEMIC_YEAR + " AS c_" + CourseTable.Columns.ACADEMIC_YEAR +
            ", c." + CourseTable.Columns.ORDER + " AS c_" + CourseTable.Columns.ORDER +
            " FROM " + AgendaTable.TABLE_NAME + " a LEFT JOIN " + CourseTable.TABLE_NAME + " c ON a." + AgendaTable.Columns.COURSE + " = c." + CourseTable.Columns.ID +
            " WHERE a." + AgendaTable.Columns.COURSE + " = :courseId" +
            " AND a." + AgendaTable.Columns.END_DATE + " >= :now" +
            " ORDER BY " + AgendaTable.Columns.START_DATE + " ASC"
    )
    List<Result> getAllFutureForCourse(String courseId, ZonedDateTime now);

    @Query("SELECT a.*, c." + CourseTable.Columns.ID + " AS c_" + CourseTable.Columns.ID +
            ", c." + CourseTable.Columns.CODE + " AS c_" + CourseTable.Columns.CODE +
            ", c." + CourseTable.Columns.TITLE + " AS c_" + CourseTable.Columns.TITLE +
            ", c." + CourseTable.Columns.DESCRIPTION + " AS c_" + CourseTable.Columns.DESCRIPTION +
            ", c." + CourseTable.Columns.TUTOR + " AS c_" + CourseTable.Columns.TUTOR +
            ", c." + CourseTable.Columns.ACADEMIC_YEAR + " AS c_" + CourseTable.Columns.ACADEMIC_YEAR +
            ", c." + CourseTable.Columns.ORDER + " AS c_" + CourseTable.Columns.ORDER +
            " FROM " + AgendaTable.TABLE_NAME + " a LEFT JOIN " + CourseTable.TABLE_NAME + " c ON a." + AgendaTable.Columns.COURSE + " = c." + CourseTable.Columns.ID +
            " WHERE a." + AgendaTable.Columns.COURSE + " = :courseId" +
            " ORDER BY " + AgendaTable.Columns.START_DATE + " ASC"
    )
    List<Result> getAllForCourse(String courseId);

    @Query("SELECT a.*, c." + CourseTable.Columns.ID + " AS c_" + CourseTable.Columns.ID +
            ", c." + CourseTable.Columns.CODE + " AS c_" + CourseTable.Columns.CODE +
            ", c." + CourseTable.Columns.TITLE + " AS c_" + CourseTable.Columns.TITLE +
            ", c." + CourseTable.Columns.DESCRIPTION + " AS c_" + CourseTable.Columns.DESCRIPTION +
            ", c." + CourseTable.Columns.TUTOR + " AS c_" + CourseTable.Columns.TUTOR +
            ", c." + CourseTable.Columns.ACADEMIC_YEAR + " AS c_" + CourseTable.Columns.ACADEMIC_YEAR +
            ", c." + CourseTable.Columns.ORDER + " AS c_" + CourseTable.Columns.ORDER +
            " FROM " + AgendaTable.TABLE_NAME + " a LEFT JOIN " + CourseTable.TABLE_NAME + " c ON a." + AgendaTable.Columns.COURSE + " = c." + CourseTable.Columns.ID +
            " WHERE (a." + AgendaTable.Columns.START_DATE + " >= :lower OR a." + AgendaTable.Columns.END_DATE + " >= :lower)" +
            " AND a." + AgendaTable.Columns.START_DATE + " <= :upper " +
            " ORDER BY " + AgendaTable.Columns.START_DATE + " ASC"
    )
    List<Result> getBetween(ZonedDateTime lower, ZonedDateTime upper);

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
