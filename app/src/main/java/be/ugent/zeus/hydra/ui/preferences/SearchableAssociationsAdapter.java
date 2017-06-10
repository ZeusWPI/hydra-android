package be.ugent.zeus.hydra.ui.preferences;

import android.util.Pair;
import android.widget.SearchView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.Association;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.MultiSelectListAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DefaultMultiSelectListViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Strijbol
 */
class SearchableAssociationsAdapter extends MultiSelectListAdapter<Association> implements SearchView.OnQueryTextListener {

    SearchableAssociationsAdapter() {
        super(R.layout.item_checkbox_string);
        this.setDataViewHolderFactory(
                itemView -> new DefaultMultiSelectListViewHolder<>(
                        itemView,
                        SearchableAssociationsAdapter.this,
                        Association::getName
                )
        );
    }

    private Map<Association, Boolean> allData = new HashMap<>();

    @Override
    public void setItems(List<Pair<Association, Boolean>> items) {
        super.setItems(items);
        for (Pair<Association, Boolean> pair : items) {
            allData.put(pair.first, pair.second);
        }
    }

    @Override
    public void setChecked(int position) {
        super.setChecked(position);
        Pair<Association, Boolean> newData = items.get(position);
        allData.put(newData.first, newData.second);
    }

    @Override
    public void setAllChecked(boolean checked) {
        super.setAllChecked(checked);
        for (Association key : allData.keySet()) {
            allData.put(key, checked);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (allData == null) {
            return true;
        }

        if (newText.isEmpty()) {
            List<Pair<Association, Boolean>> newList = new ArrayList<>();
            for (Map.Entry<Association, Boolean> pair : allData.entrySet()) {
                newList.add(new Pair<>(pair.getKey(), pair.getValue()));
            }
            this.items = newList;
            notifyDataSetChanged();
        }

        List<Pair<Association, Boolean>> newList = new ArrayList<>();

        for (Map.Entry<Association, Boolean> pair : allData.entrySet()) {
            String text = newText.toLowerCase();
            Association a = pair.getKey();
            if (a.getDisplayName().toLowerCase().contains(text) ||
                    (a.getFullName() != null && a.getFullName().toLowerCase().contains(text)) ||
                    a.getInternalName().toLowerCase().contains(text)) {
                newList.add(new Pair<>(a, pair.getValue()));
            }
        }

        //Manually update.
        this.items = newList;
        notifyDataSetChanged();

        return true;
    }

    protected Map<Association, Boolean> getAllData() {
        return allData;
    }
}
