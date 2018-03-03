package be.ugent.zeus.hydra.association.preference;

import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.ViewGroup;
import android.widget.SearchView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectSearchableAdapter;

import java.util.Collections;

/**
 *
 * @author Niko Strijbol
 */
class SearchableAssociationsAdapter extends MultiSelectSearchableAdapter<Association, AssociationViewHolder> implements SearchView.OnQueryTextListener {

    SearchableAssociationsAdapter() {
        super(text -> a -> a.getDisplayName().toLowerCase().contains(text) ||
                (a.getFullName() != null && a.getFullName().toLowerCase().contains(text)) ||
                a.getInternalName().toLowerCase().contains(text));
    }

    @NonNull
    @Override
    public AssociationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AssociationViewHolder(ViewUtils.inflate(parent, R.layout.item_checkbox_string),this);
    }

    public Iterable<Pair<Association, Boolean>> getItemsAndState() {
        return Collections.unmodifiableCollection(allData);
    }
}