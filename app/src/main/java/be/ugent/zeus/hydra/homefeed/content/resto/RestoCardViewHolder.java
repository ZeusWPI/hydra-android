package be.ugent.zeus.hydra.homefeed.content.resto;

import android.content.Intent;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.resto.MenuActivity;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.homefeed.content.HideableViewHolder;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.views.MenuTable;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Home feed view holder for the resto menu.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class RestoCardViewHolder extends HideableViewHolder {

    private final MenuTable table;

    public RestoCardViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        table = $(v, R.id.menu_table);
    }

    @Override
    public void populate(HomeCard card) {
        super.populate(card);

        RestoMenu menu = card.<RestoMenuCard>checkCard(HomeCard.CardType.RESTO).getRestoMenu();
        String text = itemView.getResources().getString(R.string.resto_menu_title);
        toolbar.setTitle(String.format(text, DateUtils.getFriendlyDate(menu.getDate())));

        table.setMenu(menu);

        // click listener
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(itemView.getContext(), MenuActivity.class);
            intent.putExtra(MenuActivity.ARG_DATE, menu.getDate());
            itemView.getContext().startActivity(intent);
        });
    }
}