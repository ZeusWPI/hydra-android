package be.ugent.zeus.hydra.ui.main.homefeed.content.resto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.resto.menu.MenuActivity;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedViewHolder;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.ui.common.widgets.MenuTable;

/**
 * Home feed view holder for the resto menu.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class RestoCardViewHolder extends FeedViewHolder {

    private final MenuTable table;
    private final SharedPreferences preferences;

    public RestoCardViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(v.getContext());
        table = v.findViewById(R.id.menu_table);
    }

    @Override
    public void populate(HomeCard card) {
        super.populate(card);

        RestoMenu menu = card.<RestoMenuCard>checkCard(HomeCard.CardType.RESTO).getRestoMenu();
        String text = itemView.getResources().getString(R.string.resto_menu_title);
        String defaultRestoName = itemView.getContext().getString(R.string.resto_default_name);
        String resto = preferences.getString(RestoPreferenceFragment.PREF_RESTO_NAME, defaultRestoName);
        toolbar.setTitle(String.format(text, DateUtils.getFriendlyDate(menu.getDate()), resto));

        table.setMenu(menu);

        // click listener
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(itemView.getContext(), MenuActivity.class);
            intent.putExtra(MenuActivity.ARG_DATE, menu.getDate());
            itemView.getContext().startActivity(intent);
        });
    }
}