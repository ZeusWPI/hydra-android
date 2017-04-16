package be.ugent.zeus.hydra.ui.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.library.Library;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.EmptyItemAdapter;
import su.j2e.rvjoiner.RvJoiner;

/**
 * Adapter for a list of libraries, with support for showing a message when there are no libraries.
 *
 * @author Niko Strijbol
 */
class LibraryListAdapter extends EmptyItemAdapter<Library, LibraryViewHolder> {

    LibraryListAdapter(RvJoiner rvJoiner) {
        super(R.layout.item_no_data, rvJoiner);
    }

    @Override
    protected LibraryViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new LibraryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false));
    }
}