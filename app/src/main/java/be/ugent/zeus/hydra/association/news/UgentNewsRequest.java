package be.ugent.zeus.hydra.association.news;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.common.request.Result;
import java9.util.Comparators;
import org.threeten.bp.Duration;

import java.util.Collections;
import java.util.List;

/**
 * Request to get UGent news.
 *
 * @author feliciaan
 */
public class UgentNewsRequest extends JsonArrayRequest<UgentNewsArticle> {

    public UgentNewsRequest(Context context) {
        super(context, UgentNewsArticle.class);
    }

    @NonNull
    @Override
    public Result<List<UgentNewsArticle>> execute(@NonNull Bundle args) {
        return super.execute(args).map(ugentNewsItems -> {
            //noinspection Java8ListSort
            Collections.sort(ugentNewsItems, Comparators.reversed(Comparators.comparing(UgentNewsArticle::getModified)));
            return ugentNewsItems;
        });
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.DSA_V3 + "recent_news.json";
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofDays(1);
    }
}