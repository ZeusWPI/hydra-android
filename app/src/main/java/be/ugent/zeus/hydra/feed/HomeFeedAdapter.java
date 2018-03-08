package be.ugent.zeus.hydra.feed;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.ItemAdapter2;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.implementations.event.EventCardViewHolder;
import be.ugent.zeus.hydra.feed.cards.implementations.minerva.announcement.MinervaAnnouncementViewHolder;
import be.ugent.zeus.hydra.feed.cards.implementations.minerva.calendar.MinervaAgendaViewHolder;
import be.ugent.zeus.hydra.feed.cards.implementations.minerva.login.MinervaLoginViewHolder;
import be.ugent.zeus.hydra.feed.cards.implementations.news.NewsItemViewHolder;
import be.ugent.zeus.hydra.feed.cards.implementations.resto.RestoCardViewHolder;
import be.ugent.zeus.hydra.feed.cards.implementations.schamper.SchamperViewHolder;
import be.ugent.zeus.hydra.feed.cards.implementations.specialevent.SpecialEventCardViewHolder;
import be.ugent.zeus.hydra.feed.cards.implementations.urgent.UrgentViewHolder;
import be.ugent.zeus.hydra.feed.commands.FeedCommand;

import static be.ugent.zeus.hydra.feed.cards.Card.Type.*;

/**
 * Adapter for {@link HomeFeedFragment}.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class HomeFeedAdapter extends ItemAdapter2<Card, DataViewHolder<Card>> {

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
        return getItem(position).hashCode();
    }

    @NonNull
    @Override
    public DataViewHolder<Card> onCreateViewHolder(@NonNull ViewGroup parent, @Card.Type int viewType) {
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
                throw new IllegalArgumentException("Non-supported view type in home feed: " + viewType);
        }
    }

    private View view(int rLayout, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(rLayout, parent, false);
    }

    @Override
    @Card.Type
    public int getItemViewType(int position) {
        return getItem(position).getCardType();
    }

    public interface AdapterCompanion extends ResultStarter {

        ActivityHelper getHelper();

        void executeCommand(FeedCommand command);
    }
}