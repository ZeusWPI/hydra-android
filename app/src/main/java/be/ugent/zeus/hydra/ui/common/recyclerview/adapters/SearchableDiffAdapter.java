package be.ugent.zeus.hydra.ui.common.recyclerview.adapters;

import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import java8.util.function.BiPredicate;
import java8.util.function.Function;
import java8.util.function.Functions;

/**
 * Searchable adapter.
 *
 * @author Niko Strijbol
 */
public abstract class SearchableDiffAdapter<D, V extends DataViewHolder<D>> extends GenericSearchableAdapter<D, V, D> {

    protected SearchableDiffAdapter(Function<D, String> stringifier) {
        this((d, s) -> stringifier.apply(d).contains(s));
    }

    protected SearchableDiffAdapter(BiPredicate<D, String> searchPredicate) {
        super(searchPredicate, Functions.identity(), Functions.identity());
    }
}