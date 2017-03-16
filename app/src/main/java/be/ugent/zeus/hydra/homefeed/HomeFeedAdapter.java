package be.ugent.zeus.hydra.homefeed;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.preferences.AssociationSelectPrefActivity;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.homefeed.content.event.EventCardViewHolder;
import be.ugent.zeus.hydra.homefeed.content.minerva.agenda.MinervaAgendaViewHolder;
import be.ugent.zeus.hydra.homefeed.content.minerva.announcement.MinervaAnnouncementViewHolder;
import be.ugent.zeus.hydra.homefeed.content.minerva.login.MinervaLoginViewHolder;
import be.ugent.zeus.hydra.homefeed.content.news.NewsItemViewHolder;
import be.ugent.zeus.hydra.homefeed.content.resto.RestoCardViewHolder;
import be.ugent.zeus.hydra.homefeed.content.schamper.SchamperViewHolder;
import be.ugent.zeus.hydra.homefeed.content.specialevent.SpecialEventCardViewHolder;
import be.ugent.zeus.hydra.homefeed.content.urgent.UrgentViewHolder;
import be.ugent.zeus.hydra.data.models.association.Association;
import be.ugent.zeus.hydra.recyclerview.adapters.common.DiffAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.utils.PreferencesUtils;
import be.ugent.zeus.hydra.utils.customtabs.ActivityHelper;

import java.lang.ref.WeakReference;

import static be.ugent.zeus.hydra.homefeed.content.HomeCard.CardType.*;

/**
 * Adapter for {@link HomeFeedFragment}.
 *
 * TODO: currently DiffResult is calculated on the main thread, investigate this.
 * Note: this used to be calculated by the loader, but this resulted in crashes due to stale data.
 * Another alternative is not using this, but calling notifyDataChanged directly. However, the docs/internet seem to
 * say it is not a problem calculating diffResult on the main thread.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class HomeFeedAdapter extends DiffAdapter<HomeCard, DataViewHolder<HomeCard>> {

    private final WeakReference<HomeFeedFragment> fragment;
    private final Context context;

    HomeFeedAdapter(HomeFeedFragment fragment) {
        super();
        this.fragment = new WeakReference<>(fragment);
        this.context = fragment.getContext().getApplicationContext();
        setHasStableIds(true);
    }

    @Nullable
    public ActivityHelper getHelper() {
        HomeFeedFragment fragment = this.fragment.get();
        if (fragment != null) {
            return fragment.getHelper();
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
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
            case URGENT_FM:
                return new UrgentViewHolder(view(R.layout.home_card_urgent, parent), this);
            case DEBUG:
            default:
                return null;
        }
    }

    private View view(int rLayout, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(rLayout, parent, false);
    }

    /**
     * Disable an association.
     *
     * @param association The association of the card to disable.
     */
    public void disableAssociation(Association association) {
        PreferencesUtils.addToStringSet(
                context,
                AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING,
                association.internalName()
        );
    }

    @Override
    public void onBindViewHolder(DataViewHolder<HomeCard> holder, int position) {
        holder.populate(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    @HomeCard.CardType
    public int getItemViewType(int position) {
        return items.get(position).getCardType();
    }

    /**
     * Disable a type of card.
     *
     * @param type The type of card to disable.
     */
    public void disableCardType(@HomeCard.CardType int type) {
        //Save preferences first
        PreferencesUtils.addToStringSet(
                context,
                HomeFeedFragment.PREF_DISABLED_CARDS,
                String.valueOf(type)
        );
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
            if (item.getItemId() == R.id.menu_hide) {
                disableCardType(type);
                return true;
            }
            return false;
        };
    }
}