package be.ugent.zeus.hydra.sko;

import android.content.Context;

import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import org.threeten.bp.Duration;

/**
 * Request SKO lineup data.
 *
 * @author Niko Strijbol
 */
class LineupRequest extends JsonArrayRequest<Artist> {

    LineupRequest(Context context) {
        super(context, Artist.class);
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofDays(1);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.SKO + "artists.json";
    }
}
