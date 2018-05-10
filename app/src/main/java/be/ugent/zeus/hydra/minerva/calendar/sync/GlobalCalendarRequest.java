package be.ugent.zeus.hydra.minerva.calendar.sync;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.minerva.common.MinervaRequest;
import okhttp3.HttpUrl;
import org.threeten.bp.ZonedDateTime;

import static be.ugent.zeus.hydra.common.network.Endpoints.MINERVA;

/**
 * Request agenda items, optionally in a time range.
 *
 * @author Niko Strijbol
 */
class GlobalCalendarRequest extends MinervaRequest<ApiCalendar> {

    private ZonedDateTime start;
    private ZonedDateTime end;

    GlobalCalendarRequest(Context context, @Nullable Account account) {
        super(context, ApiCalendar.class, account);
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        final String url = MINERVA + "agenda";

        if (start == null && end == null) {
            return MINERVA + "agenda";
        }

        HttpUrl base = HttpUrl.parse(url);
        assert base != null;
        HttpUrl.Builder builder = base.newBuilder();

        if (start != null) {
            builder.addQueryParameter("start", String.valueOf(start.toEpochSecond()));
        }

        if (end != null) {
            builder.addQueryParameter("end", String.valueOf(end.toEpochSecond()));
        }

        return builder.build().toString();
    }
}