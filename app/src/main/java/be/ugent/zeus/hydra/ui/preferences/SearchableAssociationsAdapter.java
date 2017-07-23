package be.ugent.zeus.hydra.ui.preferences;

import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;
import android.widget.SearchView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.Association;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.MultiSelectListAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DefaultMultiSelectListViewHolder;

import java.util.*;

/**
 * @author Niko Strijbol
 */
class SearchableAssociationsAdapter extends MultiSelectListAdapter<Association> implements SearchView.OnQueryTextListener {

    /**
     * Searching changes the list in the adapter. We use this to keep track of all the data. The key in the map is the
     * association, the value is the state.
     */
    private Map<Association, Boolean> allStates = new HashMap<>();
    private List<Association> allAssociations = new ArrayList<>();

    @Override
    public DataViewHolder<Pair<Association, Boolean>> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DefaultMultiSelectListViewHolder<>(
                ViewUtils.inflate(parent, R.layout.item_checkbox_string),
                SearchableAssociationsAdapter.this,
                Association::getName
        );
    }

    @Override
    public void setItems(List<Association> items) {
        super.setItems(items);
        allAssociations.clear();
        allAssociations.addAll(items);
    }

    @Override
    public void setChecked(int position) {
        super.setChecked(position);
        // Update the original data.
        allStates.put(items.get(position), isChecked(position));
    }

    @Override
    public void setAllChecked(boolean checked) {
        super.setAllChecked(checked);
        allStates.clear();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (allStates == null) {
            return true;
        }

        List<Association> newList = new ArrayList<>();
        SparseBooleanArray newArray = new SparseBooleanArray();

        // First we get all associations.
        for (Association a : allAssociations) {
            String text = newText.toLowerCase();
            if (a.getDisplayName().toLowerCase().contains(text) ||
                    (a.getFullName() != null && a.getFullName().toLowerCase().contains(text)) ||
                    a.getInternalName().toLowerCase().contains(text)) {
                if (allStates.getOrDefault(a, getDefaultValue()) != getDefaultValue()) {
                    newArray.append(newList.size(), allStates.getOrDefault(a, getDefaultValue()));
                }
                newList.add(a);
            }
        }

        //Manually update.
        this.items = newList;
        this.booleanArray = newArray;
        notifyDataSetChanged();

        return true;
    }

    @Override
    public Iterable<Pair<Association, Boolean>> getItemsAndState() {
        return () -> {
            return new Iterator<Pair<Association, Boolean>>() {

                private int current = 0;

                @Override
                public boolean hasNext() {
                    return current < allAssociations.size();
                }

                @Override
                public Pair<Association, Boolean> next() {
                    Association association = allAssociations.get(current++);
                    return new Pair<>(association, allStates.getOrDefault(association, getDefaultValue()));
                }
            };
        };
    }

    @Override
    public void setItemsAndState(List<Pair<Association, Boolean>> values) {
        super.setItemsAndState(values);
        for (Pair<Association, Boolean> pair : values) {
            allStates.put(pair.first, pair.second);
        }
    }
}