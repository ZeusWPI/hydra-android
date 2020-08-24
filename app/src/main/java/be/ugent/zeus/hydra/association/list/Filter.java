package be.ugent.zeus.hydra.association.list;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Search filter for events.
 *
 * @author Niko Strijbol
 */
public class Filter {

    private OffsetDateTime after = OffsetDateTime.now();
    private OffsetDateTime before;
    private final Set<String> associations = new HashSet<>();
    private String term;

    public Filter() {

    }

    public Set<String> getAssociations() {
        return associations;
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
        for (String association : getAssociations()) {
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

        public void setAssociations(Collection<String> associations) {
            this.filter.associations.clear();
            this.filter.associations.addAll(associations);
        }

        public void setBefore(OffsetDateTime before) {
            this.filter.before = before;
            setValue(filter);
        }

        public void setTerm(String term) {
            this.filter.term = term;
        }
    }
}
