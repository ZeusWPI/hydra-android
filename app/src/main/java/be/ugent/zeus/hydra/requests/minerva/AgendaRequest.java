package be.ugent.zeus.hydra.requests.minerva;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.models.minerva.Agenda;
import org.springframework.web.util.UriComponentsBuilder;
import org.threeten.bp.ZonedDateTime;

/**
 * Request agenda items, optionally in a time range.
 *
 * @author Niko Strijbol
 */
public class AgendaRequest extends MinervaRequest<Agenda> {

    private ZonedDateTime start;
    private ZonedDateTime end;

    public AgendaRequest(Context context, @Nullable Activity activity) {
        super(Agenda.class, context, activity);
    }

    public AgendaRequest(Context context, @Nullable Account account, @Nullable Activity activity) {
        super(Agenda.class, context, account, activity);
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
            builder.queryParam("start", start.toInstant().toEpochMilli());
        }

        if(end != null) {
            builder.queryParam("end", end.toInstant().toEpochMilli());
        }

        return builder.build().toUriString();
    }
}