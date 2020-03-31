package be.ugent.zeus.hydra.association.preference;

import androidx.annotation.NonNull;
import android.util.Pair;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.utils.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectSearchableAdapter;

import java.util.Collections;
import java.util.Locale;

/**
 *
 * @author Niko Strijbol
 */
class SearchableAssociationsAdapter extends MultiSelectSearchableAdapter<Association, AssociationViewHolder> {

    SearchableAssociationsAdapter() {
        super(text -> a -> a.getDisplayName().toLowerCase(Locale.getDefault()).contains(text) ||
                (a.getFullName() != null && a.getFullName().toLowerCase(Locale.getDefault()).contains(text)) ||
                a.getInternalName().toLowerCase(Locale.ROOT).contains(text));
    }

    @NonNull
    @Override
    public AssociationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AssociationViewHolder(ViewUtils.inflate(parent, R.layout.item_checkbox_string),this);
    }

    Iterable<Pair<Association, Boolean>> getItemsAndState() {
        return Collections.unmodifiableCollection(allData);
    }
}
