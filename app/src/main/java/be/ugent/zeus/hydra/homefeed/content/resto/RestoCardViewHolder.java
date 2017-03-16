package be.ugent.zeus.hydra.homefeed.content.resto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.resto.MenuActivity;
import be.ugent.zeus.hydra.fragments.preferences.RestoPreferenceFragment;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.homefeed.content.HideableViewHolder;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
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
    private final SharedPreferences preferences;
    private final String[] restos;

    public RestoCardViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(v.getContext());
        this.restos = v.getContext().getResources().getStringArray(R.array.resto_location);
        table = $(v, R.id.menu_table);
    }

    @Override
    public void populate(HomeCard card) {
        super.populate(card);

        RestoMenu menu = card.<RestoMenuCard>checkCard(HomeCard.CardType.RESTO).getRestoMenu();
        String text = itemView.getResources().getString(R.string.resto_menu_title);
        String resto = restos[Integer.parseInt(preferences.getString(RestoPreferenceFragment.PREF_RESTO, RestoPreferenceFragment.PREF_DEFAULT_RESTO))];
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