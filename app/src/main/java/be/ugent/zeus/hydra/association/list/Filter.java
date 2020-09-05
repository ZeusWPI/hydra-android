package be.ugent.zeus.hydra.association.list;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Search filter for events.
 *
 * @author Niko Strijbol
 */
public class Filter {

    private final Set<String> whitelist = new HashSet<>();
    private OffsetDateTime after = OffsetDateTime.now();
    private OffsetDateTime before;
    private String term;

    public Filter() {

    }

    public Set<String> getAssociationWhitelist() {
        return whitelist;
    }

    public OffsetDateTime getAfter() {
        return after;
    }

    public OffsetDateTime getBefore() {
        return before;
    }

    public String getTerm() {
        return term;
    }

    public Uri.Builder appendParams(Uri.Builder builder) {
        for (String association : getAssociationWhitelist()) {
            builder.appendQueryParameter("association[]", association);
        }
        if (getAfter() != null) {
            builder.appendQueryParameter("start_time", getAfter().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }
        if (getBefore() != null) {
            builder.appendQueryParameter("end_time", getBefore().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }
        if (getTerm() != null) {
            builder.appendQueryParameter("search_string", getTerm());
        }
        return builder;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "after=" + after +
                ", before=" + before +
                ", whitelist=" + whitelist +
                ", term='" + term + '\'' +
                '}';
    }

    public static class Live extends LiveData<Filter> {

        @NonNull
        public final Filter filter;

        public Live() {
            super(new Filter());
            assert getValue() != null;
            this.filter = getValue();
        }

        public void setAfter(OffsetDateTime after) {
            this.filter.after = after;
            setValue(filter);
        }

        public void setWhitelist(Collection<String> allowed) {
            this.filter.whitelist.clear();
            this.filter.whitelist.addAll(allowed);
        }

        public void setBefore(OffsetDateTime before) {
            this.filter.before = before;
            setValue(filter);
        }

        public boolean isWhitelisted(String abbreviation) {
            return filter.getAssociationWhitelist().contains(abbreviation);
        }

        public void setTerm(String term) {
            this.filter.term = term;
        }
    }
}
