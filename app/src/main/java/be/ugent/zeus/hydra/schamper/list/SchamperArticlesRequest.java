package be.ugent.zeus.hydra.schamper.list;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.schamper.Article;
import org.threeten.bp.Duration;

/**
 * Request to get Schamper articles.
 *
 * @author feliciaan
 */
public class SchamperArticlesRequest extends JsonArrayRequest<Article> {

    public SchamperArticlesRequest(Context context) {
        super(context, Article.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.ZEUS_V1 + "schamper/daily_android.json";
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofDays(1);
    }
}