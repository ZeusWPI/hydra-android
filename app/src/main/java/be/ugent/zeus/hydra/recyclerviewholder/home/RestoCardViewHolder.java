package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.HomeCard;
import be.ugent.zeus.hydra.models.resto.RestoMenu;

/**
 * Created by feliciaan on 06/04/16.
 */
public class RestoCardViewHolder extends AbstractViewHolder {
    public RestoCardViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCardAdapter.HomeType.RESTO) {
            return; //TODO: report errors
        }

        RestoMenu menu = (RestoMenu) card; //TODO: fill card
    }
}
