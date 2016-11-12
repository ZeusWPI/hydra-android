package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.content.Intent;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.resto.MenuActivity;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.RestoMenuCard;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.views.MenuTable;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 06/04/16.
 */
public class RestoCardViewHolder extends HideableViewHolder {

    private MenuTable table;

    public RestoCardViewHolder(View v, HomeCardAdapter adapter) {
        super(v, adapter);
        table = $(v, R.id.menu_table);
    }

    @Override
    public void populate(HomeCard card) {
        final RestoMenu menu = card.<RestoMenuCard>checkCard(HomeCard.CardType.RESTO).getRestoMenu();

        String text = itemView.getResources().getString(R.string.resto_menu_title);
        toolbar.setTitle(String.format(text, DateUtils.getFriendlyDate(menu.getDate())));

        populate(menu);

        // click listener
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(itemView.getContext(), MenuActivity.class);
            intent.putExtra(MenuActivity.ARG_DATE, menu.getDate());
            itemView.getContext().startActivity(intent);
        });

        super.populate(card);
    }

    private void populate(RestoMenu menu) {
        table.setMenu(menu);
    }
}