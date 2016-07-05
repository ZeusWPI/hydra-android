package be.ugent.zeus.hydra.models.minerva;

import be.ugent.zeus.hydra.models.converters.MinervaDateJsonAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by feliciaan on 30/06/16.
 */
public class MinervaDate implements Serializable {

    @JsonAdapter(MinervaDateJsonAdapter.class)
    private Date date;
    @SerializedName("timezone_type")
    private int timezoneType;
    private String timezone;

    public Date getDate() {
         return date;
    }

    public void setDate(Date date) {
         this.date = date;
    }

    public int getTimezoneType() {
        return timezoneType;
    }

    public void setTimezoneType(int timezone_type) {
        this.timezoneType = timezone_type;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
