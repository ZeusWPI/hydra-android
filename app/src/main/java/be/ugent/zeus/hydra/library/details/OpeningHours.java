package be.ugent.zeus.hydra.library.details;

import be.ugent.zeus.hydra.common.converter.DateThreeTenAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.squareup.moshi.Json;
import java8.util.Objects;
import org.threeten.bp.LocalDate;

import java.io.Serializable;

/**
 * @author Niko Strijbol
 */
public final class OpeningHours implements Serializable {

    @SerializedName("wday")
    @Json(name = "wday")
    private int weekday;
    @SerializedName("wday_formatted")
    @Json(name = "wday_formatted")
    private String weekdayName;
    @Json(name = "date_formatted")
    private String formattedDate;
    private String hours;
    private int month;
    @JsonAdapter(DateThreeTenAdapter.class)
    private LocalDate date;
    private String comments;
    @SerializedName("mday")
    @Json(name = "mday")
    private String monthDay;
    private String year;

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

    public String getFormattedDate() {
        return formattedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpeningHours that = (OpeningHours) o;
        return weekday == that.weekday &&
                month == that.month &&
                Objects.equals(weekdayName, that.weekdayName) &&
                Objects.equals(hours, that.hours) &&
                Objects.equals(date, that.date) &&
                Objects.equals(comments, that.comments) &&
                Objects.equals(monthDay, that.monthDay) &&
                Objects.equals(year, that.year) &&
                formattedDate.equals(that.formattedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekday, weekdayName, hours, month, date, comments, monthDay, year, formattedDate);
    }
}