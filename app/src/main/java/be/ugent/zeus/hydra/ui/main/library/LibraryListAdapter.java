package be.ugent.zeus.hydra.ui.main.library;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.SearchableDiffAdapter;
import be.ugent.zeus.hydra.library.Library;

/**
 * Adapter for a list of libraries.
 *
 * @author Niko Strijbol
 */
class LibraryListAdapter extends SearchableDiffAdapter<Library, LibraryViewHolder> {

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

    @Override
    public LibraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LibraryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false));
    }
}