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

import android.content.Context;
import android.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.arch.data.RequestLiveData;
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.library.Library;

/**
 * @author Niko Strijbol
 */
class LibraryLiveData extends RequestLiveData<List<Pair<Library, Boolean>>> implements Observer<Integer> {

    private LiveData<Integer> favouriteSource;

    LibraryLiveData(Context context) {
        super(context, makeRequest(context));
    }

    /**
     * Construct the request we want to use. This method is static because we need to call it in the constructor.
     *
     * @param context The context.
     * @return The request.
     */
    private static Request<List<Pair<Library, Boolean>>> makeRequest(Context context) {

        var repository = Database.get(context).getFavouriteRepository();

        return new LibraryListRequest(context).map(libraryList -> {
            Set<String> favourites = new HashSet<>(repository.getFavouriteIds());
            // We sort favourites first and then faculty libraries and then by name.
            return libraryList.libraries().stream()
                    .map(library -> Pair.create(library, favourites.contains(library.code())))
                    .sorted(Comparator.comparing(statusExtractor()).reversed()
                            .thenComparing(Comparator.comparing(libraryExtractor().andThen(Library::isFacultyBib)).reversed())
                            .thenComparing(libraryExtractor().andThen(Library::name))).collect(Collectors.toList());
        });
    }

    private static Function<Pair<Library, Boolean>, Boolean> statusExtractor() {
        return libraryBooleanPair -> libraryBooleanPair.second;
    }

    private static Function<Pair<Library, Boolean>, Library> libraryExtractor() {
        return libraryBooleanPair -> libraryBooleanPair.first;
    }

    @Override
    protected void onActive() {
        super.onActive();
        var repository = Database.get(context).getFavouriteRepository();
        maybeUnregister();
        favouriteSource = Transformations.distinctUntilChanged(repository.count());
        favouriteSource.observeForever(this);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        maybeUnregister();
    }

    private void maybeUnregister() {
        if (favouriteSource != null) {
            favouriteSource.removeObserver(this);
            favouriteSource = null;
        }
    }

    @Override
    public void onChanged(Integer integer) {
        loadData();
    }
}
