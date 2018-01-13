package be.ugent.zeus.hydra.ui.resto;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.resto.Sandwich;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.MultiSelectDiffAdapter;

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