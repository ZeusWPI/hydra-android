package be.ugent.zeus.hydra.recyclerviewholder.home;

import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.HomeCard;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;

/**
 * Created by feliciaan on 06/04/16.
 */
public class SpecialEventCardViewHolder extends AbstractViewHolder {
    private View view;

    public SpecialEventCardViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
    }

    @Override
    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCardAdapter.HomeType.SPECIALEVENT) {
            return; //TODO: give warning
        }

        SpecialEvent event = (SpecialEvent) card;

        TextView textView = (TextView) view.findViewById(R.id.title);
        textView.setText(event.getName());
    }
}
