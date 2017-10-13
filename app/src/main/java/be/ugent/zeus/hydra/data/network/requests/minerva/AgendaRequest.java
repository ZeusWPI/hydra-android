package be.ugent.zeus.hydra.data.network.requests.minerva;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.data.models.minerva.Agenda;
import org.springframework.web.util.UriComponentsBuilder;
import org.threeten.bp.ZonedDateTime;

/**
 * Request agenda items, optionally in a time range.
 *
 * Warning: this is an internal sync request, and should not be used to display data directly.
 *
 * @author Niko Strijbol
 */
public class AgendaRequest extends MinervaRequest<Agenda> {

    private ZonedDateTime start;
    private ZonedDateTime end;

    public AgendaRequest(Context context, @Nullable Account account) {
        super(Agenda.class, context, account);
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