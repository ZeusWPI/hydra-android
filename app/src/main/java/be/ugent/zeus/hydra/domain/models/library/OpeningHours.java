package be.ugent.zeus.hydra.domain.models.library;

import be.ugent.zeus.hydra.data.gson.DateThreeTenAdapter;
import be.ugent.zeus.hydra.data.gson.ZonedThreeTenAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java8.util.Objects;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;

/**
 * @author Niko Strijbol
 */
public final class OpeningHours implements Serializable {

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
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekday, weekdayName, hours, month, date, comments, monthDay, year, createdAt, updatedAt);
    }
}