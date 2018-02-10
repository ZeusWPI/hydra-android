package be.ugent.zeus.hydra.common.ui.recyclerview.viewholders;

import android.view.View;

/**
 * Simple view holder that does nothing.
 *
 * @author Niko Strijbol
 */
public class SimpleViewHolder<E> extends DataViewHolder<E> {

    public SimpleViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void populate(E data) {

    }
}