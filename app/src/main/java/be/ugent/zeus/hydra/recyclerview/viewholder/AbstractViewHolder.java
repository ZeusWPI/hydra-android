package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Niko Strijbol
 */
public abstract class AbstractViewHolder<D> extends RecyclerView.ViewHolder {

    public AbstractViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void populate(D data);
}