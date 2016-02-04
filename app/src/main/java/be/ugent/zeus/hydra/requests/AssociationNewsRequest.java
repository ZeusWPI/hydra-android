package be.ugent.zeus.hydra.requests;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.ResponseEntity;

import be.ugent.zeus.hydra.models.AssociationActivities;
import be.ugent.zeus.hydra.models.AssociationNews;
import be.ugent.zeus.hydra.models.AssociationNewsItem;

/**
 * Created by feliciaan on 04/02/16.
 */
public class AssociationNewsRequest extends AbstractRequest<AssociationNews>{

  public AssociationNewsRequest() {
        super(AssociationNews.class);
    }

    public String getCacheKey() {
        return "association_news";
    }

    @Override
    protected String getAPIUrl() {
        return DSA_API_URL + "all_news.json";
    }

    @Override
    public long getCacheDuration() {
        return DurationInMillis.ONE_DAY;
    }
}
