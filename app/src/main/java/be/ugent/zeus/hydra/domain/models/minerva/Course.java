package be.ugent.zeus.hydra.domain.models.minerva;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.EnumSet;

/**
 * Created by feliciaan on 21/06/16.
 */
public final class Course implements Serializable, Parcelable {

    private String id;
    private String code;
    private String title;
    private String description;
    private String tutorName;
    private int academicYear;
    private int order = 0;
    private EnumSet<Module> disabledModules;

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

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }

    public void setDisabledModules(EnumSet<Module> disabledModules) {
        this.disabledModules = disabledModules;
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
        dest.writeInt(this.academicYear);
        dest.writeInt(this.order);
        dest.writeInt(Module.toNumericalValue(disabledModules));
    }

    public Course() {
    }

    private Course(Parcel in) {
        this.id = in.readString();
        this.code = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.tutorName = in.readString();
        this.academicYear = in.readInt();
        this.order = in.readInt();
        this.disabledModules = Module.fromNumericalValue(in.readInt());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return java8.util.Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return java8.util.Objects.hash(id);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public EnumSet<Module> getDisabledModules() {
        return disabledModules;
    }

    public EnumSet<Module> getEnabledModules() {
        return EnumSet.complementOf(disabledModules);
    }
}