package be.ugent.zeus.hydra.association.preference;

import android.util.Pair;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.Locale;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectSearchableAdapter;
import be.ugent.zeus.hydra.common.utils.ViewUtils;

/**
 * @author Niko Strijbol
 */
class SearchableAssociationsAdapter extends MultiSelectSearchableAdapter<Association, AssociationViewHolder> {

    SearchableAssociationsAdapter() {
        super(text -> a -> a.getName().toLowerCase(Locale.getDefault()).contains(text) ||
                a.getAbbreviation().toLowerCase(Locale.ROOT).contains(text));
    }

    @NonNull
    @Override
    public AssociationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AssociationViewHolder(ViewUtils.inflate(parent, R.layout.item_checkbox_string), this);
    }

    Iterable<Pair<Association, Boolean>> getItemsAndState() {
        return Collections.unmodifiableCollection(allData);
    }
}
