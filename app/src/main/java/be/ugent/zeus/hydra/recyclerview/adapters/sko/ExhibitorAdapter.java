package be.ugent.zeus.hydra.recyclerview.adapters.sko;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.sko.Exhibitor;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.sko.ExhibitorViewHolder;

import java.util.ArrayList;
import java.util.Iterator;
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
     * @param list The new elements.
     */
    @Override
    public void setItems(List<Exhibitor> list) {
        super.setItems(list);
        allData = new ArrayList<>(list);
    }

    @Override
    public ExhibitorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sko_exhibitor, parent, false);
        return new ExhibitorViewHolder(v);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(allData == null) {
            return true;
        }

        if(newText.isEmpty()) {
            //Manually update.
            this.items = allData;
            notifyDataSetChanged();
        }

        List<Exhibitor> newList = new ArrayList<>(allData);

        Iterator<Exhibitor> iter = newList.iterator();
        while(iter.hasNext()) {
            Exhibitor next = iter.next();
            if(!next.getName().toLowerCase().contains(newText.toLowerCase())) {
                iter.remove();
            }
        }

        //Manually update.
        this.items = newList;
        notifyDataSetChanged();

        return true;
    }
}