package be.ugent.zeus.hydra.ui.main.library;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.library.Library;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.ItemAdapter;

/**
 * Adapter for a list of libraries.
 *
 * TODO: when extending from {@link be.ugent.zeus.hydra.ui.common.recyclerview.adapters.DiffAdapter}, it doesnt work:
 * The recyclerview ends up at the bottom of the list.
 *
 * @author Niko Strijbol
 */
class LibraryListAdapter extends ItemAdapter<Library, LibraryViewHolder> {

    @Override
    public LibraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LibraryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library, parent, false));
    }
}