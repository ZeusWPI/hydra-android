package be.ugent.zeus.hydra.minerva.course.sync;

import com.google.gson.annotations.SerializedName;

/**
 * Represents an API course.
 *
 * @author Niko Strijbol
 */
final class ApiCourse {
    public String id;
    public String code;
    public String title;
    public String description;
    @SerializedName("tutor_name")
    public String tutor;
    public int year;
}