package be.ugent.zeus.hydra.library.list;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import java9.util.Optional;
import java9.util.stream.StreamSupport;

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
            boolean contained = false;
            if (library.getName() != null && library.getName().toLowerCase().contains(s)) {
                contained = true;
            }
            if (library.getCampus() != null && library.getCampus().toLowerCase().contains(s)) {
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
        StreamSupport.stream(listeners)
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