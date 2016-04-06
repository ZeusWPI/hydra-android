package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.persistence.DurationInMillis;

import java.util.Calendar;
import java.util.Date;

import be.ugent.zeus.hydra.models.resto.RestoMenu;

/**
 * Created by Mitch on 3/03/2016.
 */
public class RestoMenuRequest extends AbstractRequest<RestoMenu> {

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
