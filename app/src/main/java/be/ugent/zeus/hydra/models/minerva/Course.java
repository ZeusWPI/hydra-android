package be.ugent.zeus.hydra.models.minerva;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by feliciaan on 21/06/16.
 */
public class Course implements Serializable, Parcelable {

    private String id;
    private String code;
    private String title;
    private String description;
    @SerializedName("tutor_name")
    private String tutorName;
    private String student;
    @SerializedName("academic_year")
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.code);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.tutorName);
        dest.writeString(this.student);
        dest.writeInt(this.academicYear);
    }

    public Course() {
    }

    private Course(Parcel in) {
        this.id = in.readString();
        this.code = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.tutorName = in.readString();
        this.student = in.readString();
        this.academicYear = in.readInt();
    }

    public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel source) {
            return new Course(source);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    /**
     * A course is equal to this course if the ID of the course is the same.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Course course = (Course) o;

        return id.equals(course.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}