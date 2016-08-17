package be.ugent.zeus.hydra.models.minerva;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by feliciaan on 21/06/16.
 */
public class Course implements Serializable {

    private String id;
    private String code;
    private String title;
    private String description;
    @SerializedName("tutor_name")
    private String tutorName;
    private String student;
    private int academicYear;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }
}