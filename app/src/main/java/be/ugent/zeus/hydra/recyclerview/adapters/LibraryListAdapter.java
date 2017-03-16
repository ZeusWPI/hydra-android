package be.ugent.zeus.hydra.recyclerview.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.library.Library;
import be.ugent.zeus.hydra.recyclerview.adapters.common.EmptyItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.LibraryViewHolder;

/**
 * Adapter for a list of libraries, with support for showing a message when there are no libraries.
 *
 * @author Niko Strijbol
 */
public class LibraryListAdapter extends EmptyItemAdapter<Library, LibraryViewHolder> {

    public LibraryListAdapter() {
        super(R.layout.item_no_data);
    }

    @Override
    protected LibraryViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new LibraryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false));
    }
}