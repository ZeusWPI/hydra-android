package be.ugent.zeus.hydra.models.library;

import be.ugent.zeus.hydra.models.converters.DateThreeTenAdapter;
import be.ugent.zeus.hydra.models.converters.ZonedThreeTenAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZonedDateTime;

/**
 * @author Niko Strijbol
 */
public class OpeningHours {

    @SerializedName("wday")
    private int weekday;

    @SerializedName("wday_formatted")
    private String weekdayName;

    private String hours;

    private int month;

    @JsonAdapter(DateThreeTenAdapter.class)
    private LocalDate date;

    private String comments;

    @SerializedName("mday")
    private String monthDay;

    private String year;

    @SerializedName("created_at")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime createdAt;

    @SerializedName("updated_at")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime updatedAt;

    public OpeningHours() {
    }

    public int getWeekday() {
        return weekday;
    }

    public String getWeekdayName() {
        return weekdayName;
    }

    public String getHours() {
        return hours;
    }

    public int getMonth() {
        return month;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getComments() {
        return comments;
    }

    public String getMonthDay() {
        return monthDay;
    }

    public String getYear() {
        return year;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }
}