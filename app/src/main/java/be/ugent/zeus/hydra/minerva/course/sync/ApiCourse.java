package be.ugent.zeus.hydra.minerva.course.sync;

import com.squareup.moshi.Json;

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
    @Json(name = "tutor_name")
    public String tutor;
    @Json(name = "academic_year")
    public int year;
}