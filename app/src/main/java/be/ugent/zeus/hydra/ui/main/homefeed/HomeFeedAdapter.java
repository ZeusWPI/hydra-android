package be.ugent.zeus.hydra.ui.main.homefeed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.domain.models.feed.Card;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.ItemDiffAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.commands.DisableTypeCommand;
import be.ugent.zeus.hydra.ui.main.homefeed.commands.FeedCommand;
import be.ugent.zeus.hydra.ui.main.homefeed.content.event.EventCardViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.agenda.MinervaAgendaViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.announcement.MinervaAnnouncementViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.login.MinervaLoginViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.news.NewsItemViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.resto.RestoCardViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.schamper.SchamperViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.specialevent.SpecialEventCardViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.urgent.UrgentViewHolder;

import static be.ugent.zeus.hydra.domain.models.feed.Card.Type.*;

/**
 * Adapter for {@link HomeFeedFragment}.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class HomeFeedAdapter extends ItemDiffAdapter<Card, DataViewHolder<Card>> {

    private final AdapterCompanion companion;

    HomeFeedAdapter(AdapterCompanion companion) {
        super();
        this.companion = companion;
        setHasStableIds(true);
    }

    public AdapterCompanion getCompanion() {
        return companion;
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    @Override
    public DataViewHolder<Card> onCreateViewHolder(ViewGroup parent, @Card.Type int viewType) {
        switch (viewType) {
            case RESTO:
                return new RestoCardViewHolder(view(R.layout.home_card_resto, parent), this);
            case ACTIVITY:
                return new EventCardViewHolder(view(R.layout.home_card_event, parent), this);
            case SPECIAL_EVENT:
                return new SpecialEventCardViewHolder(view(R.layout.home_card_special, parent), this.getCompanion());
            case SCHAMPER:
                return new SchamperViewHolder(view(R.layout.home_card_schamper, parent), this);
            case NEWS_ITEM:
                return new NewsItemViewHolder(view(R.layout.home_card_news_item, parent), this);
            case MINERVA_LOGIN:
                return new MinervaLoginViewHolder(view(R.layout.home_minerva_login_card, parent), this.getCompanion());
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

    @Override
    @Card.Type
    public int getItemViewType(int position) {
        return items.get(position).getCardType();
    }

    /**
     * Helper method that returns a listener that hides a given card type in this adapter. This will only work with the
     * default menu in {@link be.ugent.zeus.hydra.ui.common.widgets.NowToolbar}.
     *
     * @param type The type of card to hide.
     * @return A listener that will hide the given card type in this adapter.
     */
    public PopupMenu.OnMenuItemClickListener listener(@Card.Type final int type) {
        return item -> {
            if (item.getItemId() == R.id.menu_hide) {
                companion.executeCommand(new DisableTypeCommand(type));
                return true;
            }
            return false;
        };
    }

    public interface AdapterCompanion extends ResultStarter {

        ActivityHelper getHelper();

        void executeCommand(FeedCommand command);
    }
}