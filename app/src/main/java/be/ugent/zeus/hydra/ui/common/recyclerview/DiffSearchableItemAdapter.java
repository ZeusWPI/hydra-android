package be.ugent.zeus.hydra.ui.common.recyclerview;

import android.support.v7.widget.SearchView;

import java8.util.function.Function;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.List;

/**
 * Searchable adapter.
 *
 * @author Niko Strijbol
 */
public abstract class DiffSearchableItemAdapter<D, V extends DataViewHolder<D>> extends ItemDiffAdapter<D, V> implements SearchView.OnQueryTextListener {

    private List<D> allData;
    private final Function<D, String> stringifier;

    protected DiffSearchableItemAdapter(Function<D, String> stringifier) {
        this.stringifier = stringifier;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void setItems(List<D> items) {
        this.allData = items;
        super.setItems(items);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<D> filtered = StreamSupport.stream(allData)
                .filter(s -> stringifier.apply(s).contains(newText.toLowerCase()))
                .collect(Collectors.toList());
        setUpdate(filtered);
        return true;
    }

    public void setUpdate(List<D> items) {
        if (isDiffing) {
            scheduledUpdate = items;
        } else {
            updateItemInternal(items);
            isDiffing = true;
        }
    }
}