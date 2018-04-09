package be.ugent.zeus.hydra.library.list;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.SearchableAdapter;
import be.ugent.zeus.hydra.library.Library;

/**
 * Adapter for a list of libraries.
 *
 * @author Niko Strijbol
 */
class LibraryListAdapter extends SearchableAdapter<Library, LibraryViewHolder> {

    LibraryListAdapter() {
        super((library, s) -> {
            boolean contained = false;
            if (library.getName() != null && library.getName().toLowerCase().contains(s)) {
                contained = true;
            }
            if (library.getCampus() != null && library.getCampus().toLowerCase().contains(s)) {
                contained = true;
            }
            return contained;
        });
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LibraryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false));
    }
}