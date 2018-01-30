package be.ugent.zeus.hydra.minerva.calendar.sync;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.minerva.MinervaRequest;
import org.springframework.web.util.UriComponentsBuilder;
import org.threeten.bp.ZonedDateTime;

/**
 * Request agenda items, optionally in a time range.
 *
 * @author Niko Strijbol
 */
class GlobalCalendarRequest extends MinervaRequest<ApiCalendar> {

    private ZonedDateTime start;
    private ZonedDateTime end;

    GlobalCalendarRequest(Context context, @Nullable Account account) {
        super(ApiCalendar.class, context, account);
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
        final String url = MINERVA_API + "agenda";

        if(start == null && end == null) {
            return MINERVA_API + "agenda";
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        if(start != null) {
            builder.queryParam("start", start.toEpochSecond());
        }

        if(end != null) {
            builder.queryParam("end", end.toEpochSecond());
        }

        return builder.build().toUriString();
    }
}