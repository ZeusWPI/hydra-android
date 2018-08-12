package be.ugent.zeus.hydra.minerva.course.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.minerva.provider.CourseContract;
import java9.util.Objects;

/**
 * Represents a course as it is saved in the database.
 *
 * @author Niko Strijbol
 */
@Entity(tableName = CourseContract.TABLE_NAME)
public final class CourseDTO {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = CourseContract.Columns.ID)
    @SuppressWarnings("NullableProblems")
    private String id;
    @ColumnInfo(name = CourseContract.Columns.CODE)
    private String code;
    @ColumnInfo(name = CourseContract.Columns.TITLE)
    private String title;
    @ColumnInfo(name = CourseContract.Columns.DESCRIPTION)
    private String description;
    @ColumnInfo(name = CourseContract.Columns.TUTOR)
    private String tutor;
    @ColumnInfo(name = CourseContract.Columns.ACADEMIC_YEAR)
    private int year;
    @ColumnInfo(name = CourseContract.Columns.ORDER)
    private int order;
    @ColumnInfo(name = CourseContract.Columns.DISABLED_MODULES)
    private int disabledModules;
    @ColumnInfo(name = CourseContract.Columns.IGNORE_ANNOUNCEMENTS)
    private boolean ignoreAnnouncements;
    @ColumnInfo(name = CourseContract.Columns.IGNORE_CALENDAR)
    private boolean ignoreCalendar;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTutor() {
        return tutor;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setDisabledModules(int disabledModules) {
        this.disabledModules = disabledModules;
    }

    public int getDisabledModules() {
        return disabledModules;
    }

    public void setIgnoreCalendar(boolean ignoreCalendar) {
        this.ignoreCalendar = ignoreCalendar;
    }

    public void setIgnoreAnnouncements(boolean ignoreAnnouncements) {
        this.ignoreAnnouncements = ignoreAnnouncements;
    }

    public boolean getIgnoreAnnouncements() {
        return ignoreAnnouncements;
    }

    public boolean getIgnoreCalendar() {
        return ignoreCalendar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseDTO courseDTO = (CourseDTO) o;
        return Objects.equals(id, courseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}