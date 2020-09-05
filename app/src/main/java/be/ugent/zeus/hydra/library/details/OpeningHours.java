package be.ugent.zeus.hydra.library.details;

import java.time.LocalDate;
import java.util.Objects;

import com.squareup.moshi.Json;

/**
 * @author Niko Strijbol
 */
@SuppressWarnings("unused")
public final class OpeningHours {

    @Json(name = "wday")
    private int weekday;
    @Json(name = "wday_formatted")
    private String weekdayName;
    @Json(name = "date_formatted")
    private String formattedDate;
    private String hours;
    private int month;
    private LocalDate date;
    private String comments;
    @Json(name = "mday")
    private String monthDay;
    private String year;

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
                Objects.equals(formattedDate, that.formattedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekday, weekdayName, hours, month, date, comments, monthDay, year, formattedDate);
    }
}