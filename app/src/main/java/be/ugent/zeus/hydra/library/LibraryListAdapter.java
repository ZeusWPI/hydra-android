package be.ugent.zeus.hydra.library;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;

/**
 * @author Niko Strijbol
 */
public class LibraryListAdapter extends ItemAdapter<Library, LibraryViewHolder> {

    @Override
    public LibraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LibraryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false));
    }
}