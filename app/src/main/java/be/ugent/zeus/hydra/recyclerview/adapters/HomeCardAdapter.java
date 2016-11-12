package be.ugent.zeus.hydra.recyclerview.adapters;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.preferences.AssociationSelectPrefActivity;
import be.ugent.zeus.hydra.fragments.home.HomeFeedFragment;
import be.ugent.zeus.hydra.models.association.Association;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.recyclerview.viewholder.home.*;
import be.ugent.zeus.hydra.utils.PreferencesUtils;

import java.util.Collections;
import java.util.List;

import static be.ugent.zeus.hydra.models.cards.HomeCard.CardType.*;

/**
 * Adapter for {@link HomeFeedFragment}.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class HomeCardAdapter extends RecyclerView.Adapter<DataViewHolder<HomeCard>> {

    private static final String TAG = "HomeCardAdapter";

    private List<HomeCard> cardItems = Collections.emptyList();
    private final HomeFeedFragment fragment;

    public HomeCardAdapter(HomeFeedFragment fragment) {
        this.fragment = fragment;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return cardItems.get(position).hashCode();
    }

    public void setData(List<HomeCard> data, @Nullable DiffUtil.DiffResult update) {
        this.cardItems = data;
        if (update != null) {
            update.dispatchUpdatesTo(this);
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    public DataViewHolder<HomeCard> onCreateViewHolder(ViewGroup parent, @HomeCard.CardType int viewType) {
        switch (viewType) {
            case RESTO:
                return new RestoCardViewHolder(view(R.layout.home_card_resto, parent), this);
            case ACTIVITY:
                return new EventCardViewHolder(view(R.layout.home_card_event, parent), this);
            case SPECIAL_EVENT:
                return new SpecialEventCardViewHolder(view(R.layout.home_card_special, parent));
            case SCHAMPER:
                return new SchamperViewHolder(view(R.layout.home_card_schamper, parent), this);
            case NEWS_ITEM:
                return new NewsItemViewHolder(view(R.layout.home_card_news_item, parent), this);
            case MINERVA_LOGIN:
                return new MinervaLoginViewHolder(view(R.layout.home_minerva_login_card, parent));
            case MINERVA_ANNOUNCEMENT:
                return new MinervaAnnouncementViewHolder(view(R.layout.home_minerva_announcement_card, parent), this);
            case MINERVA_AGENDA:
                return new MinervaAgendaViewHolder(view(R.layout.home_minerva_agenda_card, parent), this);
        }
        return null;
    }

    private View view(int rLayout, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(rLayout, parent, false);
    }

    /**
     * Disable a type of card.
     *
     * @param type The type of card to disable.
     */
    public void disableCardType(@HomeCard.CardType int type) {

        //Save preferences first
        PreferencesUtils.addToStringSet(
                fragment.getContext(),
                HomeFeedFragment.PREF_DISABLED_CARDS,
                String.valueOf(type)
        );

        //Remove existing cards from the loader.
        fragment.getLoader().removeType(type);
    }

    /**
     * Disable an association.
     *
     * @param association The association of the card to disable.
     */
    public void disableAssociation(Association association) {

        //First save in preferences
        PreferencesUtils.addToStringSet(
                fragment.getContext(),
                AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING,
                association.getInternalName()
        );

        //Remove existing cards from the loader.
        fragment.getLoader().removeAssociations(association);
    }

    @Override
    public void onBindViewHolder(DataViewHolder<HomeCard> holder, int position) {
        HomeCard object = cardItems.get(position);
        holder.populate(object);
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    @Override
    @HomeCard.CardType
    public int getItemViewType(int position) {
        return cardItems.get(position).getCardType();
    }

    /**
     * Helper method that returns a listener that hides a given card type in this adapter. This will only work with the
     * default menu in {@link be.ugent.zeus.hydra.views.NowToolbar}.
     *
     * @param type The type of card to hide.
     * @return A listener that will hide the given card type in this adapter.
     */
    public PopupMenu.OnMenuItemClickListener listener(@HomeCard.CardType final int type) {
        return item -> {
            if(item.getItemId() == R.id.menu_hide) {
                disableCardType(type);
                return true;
            }
            return false;
        };
    }
}