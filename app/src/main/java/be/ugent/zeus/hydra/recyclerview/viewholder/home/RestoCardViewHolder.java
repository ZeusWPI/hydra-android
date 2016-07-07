package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 06/04/16.
 */
public class RestoCardViewHolder extends AbstractViewHolder {

    private TextView cardDescription;
    private ImageView cardPopup;
    private boolean added = false;

    private HomeCardAdapter adapter;

    public RestoCardViewHolder(View v, HomeCardAdapter adapter) {
        super(v);
        cardDescription = $(v, R.id.card_description);
        cardPopup       = $(v, R.id.card_description_popup);
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
                Intent intent = new Intent(itemView.getContext(), MenuActivity.class);
                intent.putExtra(MenuActivity.ARG_DATE, menu.getDate().getTime());
                itemView.getContext().startActivity(intent);
            }
        });

        cardPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardPopup(view);
            }
        });
    }

    private void cardPopup(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.inflate(R.menu.menu_home_popup);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menu_hide) {
                    adapter.disableCardType(HomeCard.CardType.RESTO);
                    return true;
                }
                return false;
            }
        });
        popup.show();
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
        textView.setText("De resto is niet open op deze dag.");

        if(added) {
            //The resto stuff is the second child
            //TODO: there must be a better way of doing this, right?
            container.removeViewAt(1);
        }

        container.addView(textView);
        added = true;
    }
}
