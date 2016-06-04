package be.ugent.zeus.hydra.preference;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author Rien Maertens
 * @since 17/04/2016.
 */
public class Time {

    private static final int MINUTES_IN_HOUR = 60;
    private int hour;
    private int minute;

    public Time(){
        clear();
    }

    public Time(Object obj){
        fromObject(obj);
    }

    public Time(String time){
        fromString(time);
    }

    public Time(int hour, int minute) {
        set(hour, minute);
    }

    public void clear(){
        hour = 0;
        minute = 0;
    }

    public void fromObject(Object obj) {
        if (obj instanceof Integer){
            fromInt((Integer) obj);
        } else  if (obj instanceof String){
            fromString((String) obj);
        } else {
            throw new AssertionError("Object is not a String or an Integer.");
        }
    }

    public void fromString(String time){
        if (time.contains(":")){
            String[] split = time.split(":");
            if (split.length == 2){
                set(Integer.parseInt(split[0], 10), Integer.parseInt(split[1], 10));
            }
        } else {
            fromInt(Integer.parseInt(time, 10));
        }
    }

    public void fromInt(int time){
        int newHour, newMinute;
        newHour = (time  / MINUTES_IN_HOUR);
        newMinute = time % MINUTES_IN_HOUR;
        set(newHour, newMinute);
    }

    public void set(int hour, int minute) {
        if (hour > 23 || minute > 59){
            throw new AssertionError("Not a valid time.");
        }
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int toInteger(){
        return hour * MINUTES_IN_HOUR + minute;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH ,"%02d:%02d", hour, minute);
    }

    public Calendar nextOccurrence(){
        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.HOUR_OF_DAY) >= hour && cal.get(Calendar.MINUTE) >= minute){
            cal.add(Calendar.DATE, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    @Override
    public int hashCode() {
        return toInteger();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Time) &&
                ((Time) o).hour == this.hour &&
                ((Time) o).minute == this.minute;
    }
}
