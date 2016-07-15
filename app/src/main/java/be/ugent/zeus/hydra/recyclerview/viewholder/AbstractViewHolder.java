package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Niko Strijbol
 * @version 15/07/2016
 */
public abstract class AbstractViewHolder<D> extends RecyclerView.ViewHolder implements DataViewHolder<D> {

    public AbstractViewHolder(View itemView) {
        super(itemView);
    }
}
