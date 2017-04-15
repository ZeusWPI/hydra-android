package be.ugent.zeus.hydra.ui.common.recyclerview.viewholders;

import android.view.View;

/**
 * Simple view holder that does nothing.
 *
 * @author Niko Strijbol
 */
public class SimpleViewHolder extends DataViewHolder<Object> {

    public SimpleViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void populate(Object data) {

    }
}