package be.ugent.zeus.hydra.recyclerview.adapters.sko;

import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.sko.Exhibitor;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.sko.ExhibitorViewHolder;
import be.ugent.zeus.hydra.utils.ViewUtils;
import java8.lang.Iterables;

import java.util.ArrayList;
import java.util.List;

/**
 * Exhibitors. Can be filtered on the name of the exhibitor.
 *
 * @author Niko Strijbol
 */
public class ExhibitorAdapter extends ItemAdapter<Exhibitor, ExhibitorViewHolder> implements
        android.widget.SearchView.OnQueryTextListener,
        android.support.v7.widget.SearchView.OnQueryTextListener {

    private List<Exhibitor> allData;

    /**
     * Set the original data set. This will save them to allow for search.
     *
     * @param items The new elements.
     */
    @Override
    public void setItems(List<Exhibitor> items) {
        super.setItems(items);
        allData = new ArrayList<>(items);
    }

    @Override
    public ExhibitorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExhibitorViewHolder(ViewUtils.inflate(parent, R.layout.item_sko_exhibitor));
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
            // Manually update.
            this.items = allData;
            notifyDataSetChanged();
        }

        List<Exhibitor> newList = new ArrayList<>(allData);
        Iterables.removeIf(newList, next -> !next.getName().toLowerCase().contains(newText.toLowerCase()));

        // Manually update.
        this.items = newList;
        notifyDataSetChanged();

        return true;
    }
}