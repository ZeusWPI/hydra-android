package be.ugent.zeus.hydra.feed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.DiffAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.event.EventCardViewHolder;
import be.ugent.zeus.hydra.feed.cards.library.LibraryViewHolder;
import be.ugent.zeus.hydra.feed.cards.news.NewsItemViewHolder;
import be.ugent.zeus.hydra.feed.cards.resto.RestoCardViewHolder;
import be.ugent.zeus.hydra.feed.cards.schamper.SchamperViewHolder;
import be.ugent.zeus.hydra.feed.cards.specialevent.SpecialEventCardViewHolder;
import be.ugent.zeus.hydra.feed.cards.urgent.UrgentViewHolder;
import be.ugent.zeus.hydra.feed.commands.FeedCommand;

import static be.ugent.zeus.hydra.feed.cards.Card.Type.*;

/**
 * Adapter for {@link HomeFeedFragment}.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class HomeFeedAdapter extends DiffAdapter<Card, DataViewHolder<Card>> {

    private final AdapterCompanion companion;

    HomeFeedAdapter(AdapterCompanion companion) {
        super();
        this.companion = companion;
        setHasStableIds(true);
    }

    private static View view(int rLayout, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(rLayout, parent, false);
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
            case URGENT_FM:
                return new UrgentViewHolder(view(R.layout.home_card_urgent, parent), this);
            case LIBRARY:
                return new LibraryViewHolder(view(R.layout.home_card_library, parent), this);
            case DEBUG:
            default:
                throw new IllegalArgumentException("Non-supported view type in home feed: " + viewType);
        }
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
