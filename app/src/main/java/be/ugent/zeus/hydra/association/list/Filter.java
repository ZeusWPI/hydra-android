package be.ugent.zeus.hydra.association.list;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.AssociationStore;

/**
 * Search filter for events.
 *
 * @author Niko Strijbol
 */
public class Filter {

    @Nullable
    private Set<String> whitelist;
    private OffsetDateTime after = OffsetDateTime.now();
    private OffsetDateTime before;
    private String term;

    public Filter() {
    }

    @NonNull
    public Optional<Set<String>> getWhitelist() {
        return Optional.ofNullable(whitelist);
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

    public void addStoredWhitelist(Context context) {
        this.whitelist = AssociationStore.read(context);
    }

    public Uri.Builder appendParams(Uri.Builder builder) {
        getWhitelist().ifPresent(strings -> {
            for (String association : strings) {
                builder.appendQueryParameter("association[]", association);
            }
            if (strings.isEmpty()) {
                builder.appendQueryParameter("association[]", "");
            }
        });
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

        public void setBefore(OffsetDateTime before) {
            this.filter.before = before;
            setValue(filter);
        }

        public boolean isWhitelisted(String abbreviation) {
            return filter.getWhitelist().orElse(Collections.emptySet()).contains(abbreviation);
        }
        
        public void setWhitelist(Set<String> whitelist) {
            this.filter.whitelist = whitelist;
            setValue(filter);
        }

        public void setTerm(String term) {
            this.filter.term = term;
            setValue(filter);
        }
    }

    public static Comparator<Pair<Association, Boolean>> selectionComparator() {
        return Comparator.comparing((Function<Pair<Association, Boolean>, Boolean>) p -> p.second)
                .reversed()
                .thenComparing(p -> p.first.getAbbreviation());
    }
}
