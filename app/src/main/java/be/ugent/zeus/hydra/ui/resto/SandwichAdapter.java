package be.ugent.zeus.hydra.ui.resto;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.MultiSelectDiffAdapter;
import be.ugent.zeus.hydra.resto.Sandwich;

/**
 * Adapter for sandwiches.
 *
 * @author Niko Strijbol
 */
public class SandwichAdapter extends MultiSelectDiffAdapter<Sandwich> {

    @Override
    public SandwichHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SandwichHolder(ViewUtils.inflate(parent, R.layout.item_sandwich), this);
    }
}