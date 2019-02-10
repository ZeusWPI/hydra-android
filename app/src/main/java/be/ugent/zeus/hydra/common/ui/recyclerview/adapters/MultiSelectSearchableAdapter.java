package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import android.util.Pair;

import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import java9.util.function.Function;
import java9.util.function.Predicate;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Basic searchable adapter with basic multi-select support. Extending a {@link SearchableAdapter} to be multi-select
 * or a {@link MultiSelectAdapter} to be searchable is very difficult and complicated, while we don't need all that
 * stuff.
 *
 * @author Niko Strijbol
 */
public abstract class MultiSelectSearchableAdapter<D, VH extends DataViewHolder<Pair<D, Boolean>>> extends DataAdapter<Pair<D, Boolean>, VH> implements
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
    public void submitData(List<Pair<D, Boolean>> data) {
        allData = new ArrayList<>(data);
        setData(data);
    }

    private void setData(List<Pair<D, Boolean>> data) {
        dataContainer.submitUpdate(new DumbUpdate<>(data));
    }

    @Override
    public void clear() {
        dataContainer.submitUpdate(new DumbUpdate<>(Collections.emptyList()));
    }

    /**
     * Change the boolean value at the given position to the reverse value. This operation DOES NOT trigger a change in
     * the recycler view data, so the methods for changed items is not called. The rationale behind this is that this
     * method is meant to be attached to a checkbox as a listener. Then the checkbox is already updated.
     *
     * @param position The position.
     */
    public void setChecked(int position) {
        dataContainer.submitUpdate(new AdapterUpdate<Pair<D, Boolean>>() {
            @Override
            public List<Pair<D, Boolean>> getNewData(List<Pair<D, Boolean>> existingData) {

                Pair<D, Boolean> data = getItem(position);
                Pair<D, Boolean> newData = new Pair<>(data.first, !data.second);

                existingData.remove(data);
                existingData.add(position, newData);

                int allDataIndex = allData.indexOf(data);
                allData.remove(data);
                allData.add(allDataIndex, newData);

                return existingData;
            }

            @Override
            public void applyUpdatesTo(ListUpdateCallback listUpdateCallback) {
                notifyItemChanged(position);
            }

            @Override
            public boolean shouldUseMultiThreading() {
                return false;
            }
        });
    }

    /**
     * Set all items to the given boolean. This operation DOES trigger a layout update.
     *
     * @param checked The value.
     */
    public void setAllChecked(boolean checked) {
        dataContainer.submitUpdate(new AdapterUpdate<Pair<D, Boolean>>() {
            @Override
            @Nullable
            public List<Pair<D, Boolean>> getNewData(@Nullable List<Pair<D, Boolean>> existingData) {

                if (existingData == null) {
                    return null;
                }

                allData = StreamSupport.stream(allData)
                        .map(p -> new Pair<>(p.first, checked))
                        .collect(Collectors.toList());

                return StreamSupport.stream(existingData)
                        .map(p -> new Pair<>(p.first, checked))
                        .collect(Collectors.toList());
            }

            @Override
            public void applyUpdatesTo(ListUpdateCallback listUpdateCallback) {
                notifyItemRangeChanged(0, getItemCount());
            }

            @Override
            public boolean shouldUseMultiThreading() {
                return false;
            }
        });
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Predicate<D> predicate = searchPredicate.apply(newText);
        List<Pair<D, Boolean>> newData = StreamSupport.stream(allData)
                .filter(dBooleanPair -> predicate.test(dBooleanPair.first))
                .collect(Collectors.toList());
        dataContainer.submitUpdate(new DumbUpdate<>(newData));
        return true;
    }
}