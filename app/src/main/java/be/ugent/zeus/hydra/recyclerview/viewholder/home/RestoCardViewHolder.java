package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.resto.MenuActivity;
import be.ugent.zeus.hydra.fragments.resto.MenuFragment;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.RestoMenuCard;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.views.NowToolbar;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 06/04/16.
 */
public class RestoCardViewHolder extends AbstractViewHolder {

    private boolean added = false;

    private NowToolbar toolbar;

    private HomeCardAdapter adapter;

    public RestoCardViewHolder(View v, HomeCardAdapter adapter) {
        super(v);
        toolbar         = $(v, R.id.card_now_toolbar);
        this.adapter = adapter;
    }

    @Override
    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCard.CardType.RESTO) {
            return; //TODO: report errors
        }

        final RestoMenuCard menuCard = (RestoMenuCard) card;
        final RestoMenu menu = menuCard.getRestoMenu();

        String text = itemView.getResources().getString(R.string.resto_menu_title);
        toolbar.setTitle(String.format(text, DateUtils.getFriendlyDate(menu.getDate())));

        if (menu.isOpen()) {
            populateOpen(menu);
        } else {
            populateClosed();
        }

        toolbar.setOnClickListener(adapter.listener(HomeCard.CardType.RESTO));

        // click listener
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), MenuActivity.class);
                intent.putExtra(MenuActivity.ARG_DATE, menu.getDate().getTime());
                itemView.getContext().startActivity(intent);
            }
        });

    }

    private void populateOpen(RestoMenu menu) {
        LinearLayout container = $(itemView, R.id.home_cards_resto_container);
        if(added) {
            //The resto stuff is the second child
            //TODO: there must be a better way of doing this, right?
            container.removeViewAt(1);
        }

        container.addView(MenuFragment.makeTableDishes(container, menu.getMainDishes()));
        added = true;
    }

    private void populateClosed() {
        LinearLayout container = $(itemView, R.id.home_cards_resto_container);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TextView textView = new TextView(itemView.getContext());
        textView.setLayoutParams(lp);
        textView.setText("De resto is gesloten op deze dag.");

        if(added) {
            //The resto stuff is the second child
            //TODO: there must be a better way of doing this, right?
            container.removeViewAt(1);
        }

        container.addView(textView);
        added = true;
    }
}
