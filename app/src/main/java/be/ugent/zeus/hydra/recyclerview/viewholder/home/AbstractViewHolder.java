package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import be.ugent.zeus.hydra.models.cards.HomeCard;


/**
 * Created by feliciaan on 06/04/16.
 */
public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {
    public AbstractViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void populate(HomeCard card);
}
