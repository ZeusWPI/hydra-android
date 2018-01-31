package be.ugent.zeus.hydra.minerva.course.database;

import android.arch.persistence.room.*;
import android.support.annotation.VisibleForTesting;

import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementTable;

import java.util.Collection;
import java.util.List;

/**
 * @author Niko Strijbol
 */
@Dao
public interface CourseDao {

    @Query("SELECT * FROM " + CourseTable.TABLE_NAME + " WHERE " + CourseTable.Columns.ID + " IS :id")
    CourseDTO getOne(String id);

    @Query("SELECT * FROM " + CourseTable.TABLE_NAME)
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

    @Query("DELETE FROM " + CourseTable.TABLE_NAME)
    void deleteAll();

    @Query("DELETE FROM " + CourseTable.TABLE_NAME + " WHERE " + CourseTable.Columns.ID + " IS :id")
    void delete(String id);

    @Query("DELETE FROM " + CourseTable.TABLE_NAME + " WHERE " + CourseTable.Columns.ID + " IN (:ids)")
    void deleteById(List<String> ids);

    @Query("SELECT * FROM " + CourseTable.TABLE_NAME + " WHERE " + CourseTable.Columns.ID + " IN (:ids)")
    List<CourseDTO> getIn(List<String> ids);

    @Query("SELECT " + CourseTable.TABLE_NAME + ".*, (SELECT count(*) FROM " + AnnouncementTable.TABLE_NAME + " WHERE " +
            AnnouncementTable.Columns.COURSE + " = " + CourseTable.TABLE_NAME + "." + CourseTable.Columns.ID + " AND " +
            AnnouncementTable.Columns.READ_DATE + " ISNULL) AS unread_count FROM " + CourseTable.TABLE_NAME +
            " ORDER BY " + CourseTable.TABLE_NAME + "." + CourseTable.Columns.ORDER + " ASC, " + CourseTable.TABLE_NAME + "." + CourseTable.Columns.TITLE + " ASC")
    List<CourseUnread> getAllAndUnreadInOrder();

    @Query("SELECT " + CourseTable.Columns.ID + " FROM " + CourseTable.TABLE_NAME)
    List<String> getIds();

    @Query("SELECT " + CourseTable.Columns.ID + ", " + CourseTable.Columns.ORDER + ", " + CourseTable.Columns.DISABLED_MODULES + " FROM " + CourseTable.TABLE_NAME)
    List<IdAndLocalData> getIdsAndLocalData();

    class IdAndLocalData {
        @ColumnInfo(name = CourseTable.Columns.ID)
        public String id;
        @ColumnInfo(name = CourseTable.Columns.ORDER)
        public int order;
        @ColumnInfo(name = CourseTable.Columns.DISABLED_MODULES)
        public int disabledModules;
        @VisibleForTesting
        public IdAndLocalData(String id, int order, int disabledModules) {
            this.id = id;
            this.order = order;
            this.disabledModules = disabledModules;
        }
    }
}