package be.ugent.zeus.hydra.data.dto.minerva;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.database.minerva2.course.CourseTable;
import com.google.gson.annotations.SerializedName;
import java8.util.Objects;

/**
 * Represents a course as it is saved in the database.
 *
 * @author Niko Strijbol
 */
@Entity(tableName = CourseTable.TABLE_NAME)
public final class CourseDTO {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = CourseTable.Columns.ID)
    private String id;
    @ColumnInfo(name = CourseTable.Columns.CODE)
    private String code;
    @ColumnInfo(name = CourseTable.Columns.TITLE)
    private String title;
    @ColumnInfo(name = CourseTable.Columns.DESCRIPTION)
    private String description;
    @SerializedName("tutor_name")
    @ColumnInfo(name = CourseTable.Columns.TUTOR)
    private String tutor;
    @SerializedName("academic_year")
    @ColumnInfo(name = CourseTable.Columns.ACADEMIC_YEAR)
    private int year;
    @ColumnInfo(name = CourseTable.Columns.ORDER)
    private int order = 0;

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