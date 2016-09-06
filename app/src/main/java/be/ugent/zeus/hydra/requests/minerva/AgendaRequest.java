package be.ugent.zeus.hydra.requests.minerva;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.models.minerva.Agenda;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;

/**
 * Request agenda items, optionally in a time range.
 *
 * @author Niko Strijbol
 */
public class AgendaRequest extends MinervaRequest<Agenda> {

    private Date start;
    private Date end;

    public AgendaRequest(Context context, @Nullable Activity activity) {
        super(Agenda.class, context, activity);
    }

    public AgendaRequest(Context context, @Nullable Account account, @Nullable Activity activity) {
        super(Agenda.class, context, account, activity);
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
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
            builder.queryParam("start", start.getTime());
        }

        if(end != null) {
            builder.queryParam("end", end.getTime());
        }

        return builder.build().toUriString();
    }
}