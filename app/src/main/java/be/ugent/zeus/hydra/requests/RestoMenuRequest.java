package be.ugent.zeus.hydra.requests;

import android.provider.CalendarContract;

import com.octo.android.robospice.persistence.DurationInMillis;

import java.util.Calendar;
import java.util.Date;

import be.ugent.zeus.hydra.models.resto.RestoMenu;

/**
 * Created by Mitch on 3/03/2016.
 */
public class RestoMenuRequest extends AbstractRequest<RestoMenu> {

    // format time
    //private final SimpleDateFormat yearFormatter = new SimpleDateFormat("YYYY", Locale.ENGLISH);
    //private final SimpleDateFormat monthFormatter = new SimpleDateFormat("MM", Locale.ENGLISH);
    // private final SimpleDateFormat dayFormatter = new SimpleDateFormat("DD", Locale.ENGLISH);

    private Date date;
    private String year;
    private String month;
    private String day;

    public RestoMenuRequest(Date date) {
        super(RestoMenu.class);
        this.date = date;
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        this.year = c.get(Calendar.YEAR) + "";
        this.month = c.get(Calendar.MONTH) + "";
        this.day = c.get(Calendar.DAY_OF_WEEK) + "";

        //this.year = yearFormatter.format(date);
        //this.month = monthFormatter.format(date);
        //this.day = dayFormatter.format(date);
    }

    @Override
    public String getCacheKey() {
        return "menu.json";
    }

    @Override
    protected String getAPIUrl() {
        return ZEUS_API_URL + "2.0/resto/menu/nl/" + year + "/" + month + "/" + day + ".json";
    }

    @Override
    public long getCacheDuration() {
        return DurationInMillis.ONE_MINUTE * 15;
    }
}
