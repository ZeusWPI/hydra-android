package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.persistence.DurationInMillis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import be.ugent.zeus.hydra.models.Resto.RestoMenu;

/**
 * Created by Mitch on 3/03/2016.
 */
public class RestoMenuRequest extends AbstractRequest<RestoMenu> {

    // format time
    //private final SimpleDateFormat yearFormatter = new SimpleDateFormat("YY", Locale.ENGLISH);
    // private final SimpleDateFormat monthFormatter = new SimpleDateFormat("MM", Locale.ENGLISH);
    //private final SimpleDateFormat dayFormatter = new SimpleDateFormat("DD", Locale.ENGLISH);

    private Date date;
    private String year;
    private String month;
    private String day;

    public RestoMenuRequest(Date date) {
        super(RestoMenu.class);
        this.date = date;
        //this.year = yearFormatter.format(date);
        //this.month = monthFormatter.format(date);
        //this.day = dayFormatter.format(date);

        this.year = "2016";
        this.month = "2";
        this.day = "29";
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
