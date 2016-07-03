package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.content.Intent;
import android.view.View;

import be.ugent.zeus.hydra.activities.AuthenticationActivity;
import be.ugent.zeus.hydra.models.cards.HomeCard;

/**
 * Created by feliciaan on 29/06/16.
 */
public class MinervaLoginViewHolder extends AbstractViewHolder {

    public MinervaLoginViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void populate(HomeCard card) {
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(itemView.getContext(), AuthenticationActivity.class);
                itemView.getContext().startActivity(intent);
            }
        });
    }
}
