/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.association.common;

import android.content.Context;
import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import be.ugent.zeus.hydra.association.Association;

/**
 * Search filter for event requests.
 *
 * @author Niko Strijbol
 */
public class EventFilter {
    private List<Pair<Association, Boolean>> selectedAssociations;
    private OffsetDateTime after = OffsetDateTime.now();
    private OffsetDateTime before;
    private String term;

    public EventFilter() {
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
    
    public EventRequest.Filter toRequestFilter(Context context, List<Association> associations) {
        Set<String> whitelist = AssociationVisibilityStorage.calculateWhitelist(context, associations, this.selectedAssociations);
        
        EventRequest.Filter requestFilter = new EventRequest.Filter(whitelist);
        requestFilter.setAfter(this.after);
        requestFilter.setBefore(this.before);
        requestFilter.setTerm(this.term);
        
        return requestFilter;
    }

    public static class Live extends LiveData<EventFilter> {

        @NonNull
        public final EventFilter filter;

        public Live() {
            super(new EventFilter());
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

        public void setSelectedAssociations(List<Pair<Association, Boolean>> selectedAssociations) {
            this.filter.selectedAssociations = selectedAssociations;
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
