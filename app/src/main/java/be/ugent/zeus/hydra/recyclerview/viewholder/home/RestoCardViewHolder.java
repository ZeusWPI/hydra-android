package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.Hydra;
import be.ugent.zeus.hydra.fragments.resto.MenuFragment;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.RestoMenuCard;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.utils.DateUtils;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 06/04/16.
 */
public class RestoCardViewHolder extends AbstractViewHolder {

    private TextView cardDescription;
    private boolean added = false;

    public RestoCardViewHolder(View v) {
        super(v);
        cardDescription = $(v, R.id.card_description);
    }

    @Override
    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCard.CardType.RESTO) {
            return; //TODO: report errors
        }

        final RestoMenuCard menuCard = (RestoMenuCard) card;
        final RestoMenu menu = menuCard.getRestoMenu();

        String text = itemView.getResources().getString(R.string.resto_menu_title);
        cardDescription.setText(String.format(text, DateUtils.getFriendlyDate(menu.getDate())));

        if (menu.isOpen()) {
            populateOpen(menu);
        } else {
            populateClosed();
        }

        // click listener
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: open resto fragment
                if (v.getContext() instanceof Hydra) {
                    Hydra activity = (Hydra) v.getContext();
                    activity.changeFragment(2);
                }
            }
        });
    }

    private void populateOpen(RestoMenu menu) {
        LinearLayout container = $(itemView, R.id.home_cards_resto_container);
        if(!added) {
            container.addView(MenuFragment.makeTableDishes(container, menu.getMainDishes()));
            added = true;
        }

    }

    private void populateClosed() {
        LinearLayout container = $(itemView, R.id.home_cards_resto_container);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TextView textView = new TextView(itemView.getContext());
        textView.setLayoutParams(lp);
        textView.setText("De resto is niet open op deze dag.");

        if(!added) {
            container.addView(textView);
            added = true;
        }

    }
}
