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

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.SearchableAdapter;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.details.OpeningHours;

/**
 * Adapter for a list of libraries.
 *
 * @author Niko Strijbol
 */
class LibraryListAdapter extends SearchableAdapter<Pair<Library, Boolean>, LibraryViewHolder> {

    private final List<Pair<LiveData<Result<Optional<OpeningHours>>>, Observer<Result<Optional<OpeningHours>>>>> listeners = new ArrayList<>();
    private final LibraryViewModel viewModel;

    LibraryListAdapter(LibraryViewModel viewModel) {
        super((pair, s) -> {
            Library library = pair.first;
            boolean contained = library.getName() != null && library.getName().toLowerCase(Locale.getDefault()).contains(s);
            if (library.getCampus() != null && library.getCampus().toLowerCase(Locale.getDefault()).contains(s)) {
                contained = true;
            }
            return contained;
        });
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LibraryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false), this);
    }

    void registerListener(Library library, Observer<Result<Optional<OpeningHours>>> listener) {
        LiveData<Result<Optional<OpeningHours>>> liveData = viewModel.getOpeningHours(library);
        listeners.add(new Pair<>(liveData, listener));
        liveData.observeForever(listener);
    }

    void unregisterListener(Observer<Result<Optional<OpeningHours>>> listener) {
        // Find the pair.
        listeners.stream()
                .filter(p -> p.second == listener)
                .findFirst().ifPresent(p -> {
            p.first.removeObserver(p.second);
            listeners.remove(p);
        });
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        // TODO: this is not called?
        super.onDetachedFromRecyclerView(recyclerView);
        // Ensure there are no more listeners
        clearObservers();
    }

    void clearObservers() {
        for (Pair<LiveData<Result<Optional<OpeningHours>>>, Observer<Result<Optional<OpeningHours>>>> pair : listeners) {
            pair.first.removeObserver(pair.second);
        }
        listeners.clear();
    }
}