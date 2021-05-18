package be.ugent.zeus.hydra.news;

import android.content.Context;
import androidx.annotation.NonNull;

import java.time.Duration;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;

/**
 * Request to get UGent news.
 *
 * @author feliciaan
 */
public class NewsRequest extends JsonOkHttpRequest<NewsStream> {
    
    private final Context context;

    public NewsRequest(Context context) {
        super(context, NewsStream.class);
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        String endpoint = context.getString(R.string.ugent_news_endpoint);
        return Endpoints.ZEUS_V2 + endpoint;
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofDays(1);
    }
}
