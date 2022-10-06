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

package be.ugent.zeus.hydra.association.list;

import android.app.Application;
import android.util.Pair;
import androidx.annotation.NonNull;

import java.util.List;

import be.ugent.zeus.hydra.association.common.AssociationMap;
import be.ugent.zeus.hydra.association.common.AssociationRequestBuilder;
import be.ugent.zeus.hydra.association.common.EventFilter;
import be.ugent.zeus.hydra.association.common.EventItem;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

/**
 * @author Niko Strijbol
 */
public class EventViewModel extends RequestViewModel<Pair<AssociationMap, List<EventItem>>> {

    private EventFilter filter;

    public EventViewModel(Application application) {
        super(application);
    }

    public void setParams(EventFilter filter) {
        this.filter = filter;
    }

    @NonNull
    @Override
    protected Request<Pair<AssociationMap, List<EventItem>>> getRequest() {
        return AssociationRequestBuilder.createItemFilteredEventRequest(getApplication(), filter);
    }
}