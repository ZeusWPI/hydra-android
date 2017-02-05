package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.view.View;

/**
 * Simple view holder that does nothing.
 *
 * @author Niko Strijbol
 */
public class SimpleViewHolder extends DataViewHolder<Void> {

    public SimpleViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void populate(Void data) {
        // Do nothing.
    }
}