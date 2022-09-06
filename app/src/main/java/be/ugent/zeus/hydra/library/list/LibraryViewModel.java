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

package be.ugent.zeus.hydra.library.list;

import android.app.Application;
import android.os.Bundle;
import android.util.Pair;
import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.*;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.arch.data.RequestLiveData;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.SingleRefreshViewModel;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.details.OpeningHours;
import be.ugent.zeus.hydra.library.details.OpeningHoursRequest;

/**
 * @author Niko Strijbol
 */
public class LibraryViewModel extends SingleRefreshViewModel<List<Pair<Library, Boolean>>> {

    private final Map<String, LiveData<Result<Optional<OpeningHours>>>> mapping = new HashMap<>();

    public LibraryViewModel(Application application) {
        super(application);
    }

    @Override
    protected BaseLiveData<Result<List<Pair<Library, Boolean>>>> constructDataInstance() {
        return new LibraryLiveData(getApplication());
    }

    LiveData<Result<Optional<OpeningHours>>> getOpeningHours(Library library) {
        return mapping.computeIfAbsent(library.getCode(), s -> {
            OpeningHoursRequest request = new OpeningHoursRequest(getApplication(), library);
            LocalDate today = LocalDate.now();
            return new RequestLiveData<>(getApplication(), request.forDay(today));
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mapping.clear();
    }

    @Override
    public void requestRefresh() {
        super.requestRefresh();
        for (LiveData<?> liveData : mapping.values()) {
            if (liveData instanceof BaseLiveData) {
                ((BaseLiveData<?>) liveData).flagForRefresh();
            }
        }
    }

    @Override
    public void requestRefresh(Bundle args) {
        super.requestRefresh(args);
        for (LiveData<?> liveData : mapping.values()) {
            if (liveData instanceof BaseLiveData) {
                ((BaseLiveData<?>) liveData).flagForRefresh(args);
            }
        }
    }
}