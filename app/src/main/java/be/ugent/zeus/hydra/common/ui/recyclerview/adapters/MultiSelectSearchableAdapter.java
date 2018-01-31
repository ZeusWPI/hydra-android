package be.ugent.zeus.hydra.ui.common.recyclerview.adapters;

import android.support.v7.widget.SearchView;
import android.util.Pair;

import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import java8.util.function.Function;
import java8.util.function.Predicate;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Basic searchable adapter with basic multi-select support. Extending a {@link SearchableDiffAdapter} to be multi-select
 * or a {@link MultiSelectDiffAdapter} to be searchable is very difficult and complicated, while we don't need all that
 * stuff.
 *
 * @author Niko Strijbol
 */
public abstract class MultiSelectSearchableAdapter<D, VH extends DataViewHolder<Pair<D, Boolean>>> extends ItemAdapter<Pair<D, Boolean>, VH> implements
        SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {

    protected List<Pair<D, Boolean>> allData = Collections.emptyList();

    private final Function<String, Predicate<D>> searchPredicate;

    protected MultiSelectSearchableAdapter(Function<String, Predicate<D>> searchPredicate) {
        this.searchPredicate = searchPredicate;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void setItems(List<Pair<D, Boolean>> items) {
        allData = new ArrayList<>(items);
        super.setItems(items);
    }

    /**
     * Change the boolean value at the given position to the reverse value. This operation DOES NOT trigger a change in
     * the recycler view data, so the methods for changed items is not called. The rationale behind this is that this
     * method is meant to be attached to a checkbox as a listener. Then the checkbox is already updated.
     *
     * @param position The position.
     */
    public void setChecked(int position) {
        Pair<D, Boolean> data = items.get(position);
        Pair<D, Boolean> newData = new Pair<>(data.first, !data.second);

        items.remove(data);
        items.add(position, newData);

        int allDataIndex = allData.indexOf(data);
        allData.remove(data);
        allData.add(allDataIndex, newData);

        notifyItemChanged(position);
    }

    /**
     * Set all items to the given boolean. This operation DOES trigger a layout update.
     *
     * @param checked The value.
     */
    public void setAllChecked(boolean checked) {

        List<Pair<D, Boolean>> newList = new ArrayList<>();
        List<Pair<D, Boolean>> newAllData = new ArrayList<>();

        for (Pair<D, Boolean> oldPair : items) {
            newList.add(new Pair<>(oldPair.first, checked));
        }
        for (Pair<D, Boolean> oldPair: allData) {
            newAllData .add(new Pair<>(oldPair.first, checked));
        }

        this.allData = newAllData;
        this.items = newList;
        notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Predicate<D> predicate = searchPredicate.apply(newText);
        this.items = StreamSupport.stream(allData)
                .filter(dBooleanPair -> predicate.test(dBooleanPair.first))
                .collect(Collectors.toList());
        notifyDataSetChanged();
        return true;
    }
}