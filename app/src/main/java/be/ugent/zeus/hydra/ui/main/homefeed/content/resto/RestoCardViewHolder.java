package be.ugent.zeus.hydra.ui.main.homefeed.content.resto;

import android.content.Intent;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.data.network.requests.resto.SelectableMetaRequest;
import be.ugent.zeus.hydra.ui.common.widgets.MenuTable;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.ui.resto.menu.MenuActivity;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * Home feed view holder for the resto menu.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class RestoCardViewHolder extends FeedViewHolder {

    private final MenuTable table;

    public RestoCardViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        table = v.findViewById(R.id.menu_table);
    }

    @Override
    public void populate(HomeCard card) {
        super.populate(card);

        RestoMenuCard menuCard = card.<RestoMenuCard>checkCard(HomeCard.CardType.RESTO);
        RestoMenu menu = menuCard.getRestoMenu();
        SelectableMetaRequest.RestoChoice choice = menuCard.getRestoChoice();
        String text = itemView.getResources().getString(R.string.resto_menu_title);
        toolbar.setTitle(String.format(text, DateUtils.getFriendlyDate(menu.getDate()), choice.getName()));

        table.setMenu(menu);

        // click listener
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(itemView.getContext(), MenuActivity.class);
            intent.putExtra(MenuActivity.ARG_DATE, menu.getDate());
            itemView.getContext().startActivity(intent);
        });
    }
}