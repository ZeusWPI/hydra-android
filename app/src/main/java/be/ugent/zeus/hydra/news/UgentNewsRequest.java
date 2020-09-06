package be.ugent.zeus.hydra.news;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * Request to get UGent news.
 *
 * @author feliciaan
 */
public class UgentNewsRequest extends JsonArrayRequest<UgentNewsArticle> {
    
    private final Context context;

    public UgentNewsRequest(Context context) {
        super(context, UgentNewsArticle.class);
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Result<List<UgentNewsArticle>> execute(@NonNull Bundle args) {
        return super.execute(args).map(ugentNewsItems -> {
            Collections.sort(ugentNewsItems, Comparator.comparing(UgentNewsArticle::getModified).reversed());
            return ugentNewsItems;
        });
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        String endpoint = context.getString(R.string.ugent_news_endpoint);
        return Endpoints.UGENT + endpoint;
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofDays(1);
    }
}
