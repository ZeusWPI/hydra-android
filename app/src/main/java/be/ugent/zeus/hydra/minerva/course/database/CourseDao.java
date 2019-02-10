package be.ugent.zeus.hydra.minerva.course.database;

import androidx.room.*;
import androidx.annotation.VisibleForTesting;

import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementTable;
import be.ugent.zeus.hydra.minerva.provider.CourseContract;

import java.util.Collection;
import java.util.List;

/**
 * @author Niko Strijbol
 */
@Dao
public interface CourseDao {

    @Query("SELECT * FROM " + CourseContract.TABLE_NAME + " WHERE " + CourseContract.Columns.ID + " IS :id")
    CourseDTO getOne(String id);

    @Query("SELECT * FROM " + CourseContract.TABLE_NAME)
    List<CourseDTO> getAll();

    @Insert
    void insert(CourseDTO course);

    @Insert
    void insert(Collection<CourseDTO> courses);

    @Update
    void update(CourseDTO course);

    @Update
    void update(Collection<CourseDTO> courses);

    @Delete
    void delete(CourseDTO course);

    @Delete
    void delete(Collection<CourseDTO> courses);

    @Query("DELETE FROM " + CourseContract.TABLE_NAME)
    void deleteAll();

    @Query("DELETE FROM " + CourseContract.TABLE_NAME + " WHERE " + CourseContract.Columns.ID + " IS :id")
    void delete(String id);

    @Query("DELETE FROM " + CourseContract.TABLE_NAME + " WHERE " + CourseContract.Columns.ID + " IN (:ids)")
    void deleteById(List<String> ids);

    @Query("SELECT * FROM " + CourseContract.TABLE_NAME + " WHERE " + CourseContract.Columns.ID + " IN (:ids)")
    List<CourseDTO> getIn(List<String> ids);

    @Query("SELECT " + CourseContract.TABLE_NAME + ".*, (SELECT count(*) FROM " + AnnouncementTable.TABLE_NAME + " WHERE " +
            AnnouncementTable.Columns.COURSE + " = " + CourseContract.TABLE_NAME + "." + CourseContract.Columns.ID + " AND " +
            AnnouncementTable.Columns.READ_DATE + " ISNULL) AS unread_count FROM " + CourseContract.TABLE_NAME +
            " ORDER BY " + CourseContract.TABLE_NAME + "." + CourseContract.Columns.ORDER + " ASC, " + CourseContract.TABLE_NAME + "." + CourseContract.Columns.TITLE + " ASC")
    List<CourseUnread> getAllAndUnreadInOrder();

    @Query("SELECT " + CourseContract.Columns.ID + " FROM " + CourseContract.TABLE_NAME)
    List<String> getIds();

    @Query("SELECT " + CourseContract.Columns.ID +
            ", " + CourseContract.Columns.ORDER +
            ", " + CourseContract.Columns.DISABLED_MODULES +
            ", " + CourseContract.Columns.IGNORE_ANNOUNCEMENTS +
            ", " + CourseContract.Columns.IGNORE_CALENDAR +
            " FROM " + CourseContract.TABLE_NAME)
    List<IdAndLocalData> getIdsAndLocalData();

    class IdAndLocalData {
        @ColumnInfo(name = CourseContract.Columns.ID)
        public final String id;
        @ColumnInfo(name = CourseContract.Columns.ORDER)
        public final int order;
        @ColumnInfo(name = CourseContract.Columns.DISABLED_MODULES)
        public final int disabledModules;
        @ColumnInfo(name = CourseContract.Columns.IGNORE_ANNOUNCEMENTS)
        public final boolean ignoreAnnouncements;
        @ColumnInfo(name = CourseContract.Columns.IGNORE_CALENDAR)
        public final boolean ignoreCalendar;
        @VisibleForTesting
        public IdAndLocalData(String id, int order, int disabledModules, boolean ignoreAnnouncements, boolean ignoreCalendar) {
            this.id = id;
            this.order = order;
            this.disabledModules = disabledModules;
            this.ignoreAnnouncements = ignoreAnnouncements;
            this.ignoreCalendar = ignoreCalendar;
        }
    }
}