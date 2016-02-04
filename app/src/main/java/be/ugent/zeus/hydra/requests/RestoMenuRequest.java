package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import be.ugent.zeus.hydra.models.Resto.RestoWeek;

/**
 * Created by feliciaan on 15/10/15.
 */
public class RestoMenuRequest extends SpringAndroidSpiceRequest<RestoWeek> {

    private static final String RESTO_API_URL = "https://zeus.ugent.be/hydra/api/1.0/resto/menu/{year}/{week}.json";
    private Date date;

    public RestoMenuRequest(Date date) {
        super(RestoWeek.class);
        this.date = date;
    }

    @Override
    public RestoWeek loadDataFromNetwork() throws Exception {

        Map<String, String> vars = new HashMap<>();
        vars.put("year", "2015");
        vars.put("week", "43");
        ResponseEntity<RestoWeek> result =  getRestTemplate().getForEntity(RESTO_API_URL, RestoWeek.class, vars);
        System.out.println(result.getHeaders());
        return result.getBody();
    }

    public String createCacheKey() {
        return "week" + 43;
    }
}
