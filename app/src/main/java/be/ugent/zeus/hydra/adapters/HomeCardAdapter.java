package be.ugent.zeus.hydra.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerviewholder.home.AbstractViewHolder;
import be.ugent.zeus.hydra.recyclerviewholder.home.ActivityCardViewHolder;
import be.ugent.zeus.hydra.recyclerviewholder.home.MinervaLoginViewHolder;
import be.ugent.zeus.hydra.recyclerviewholder.home.NewsItemViewHolder;
import be.ugent.zeus.hydra.recyclerviewholder.home.RestoCardViewHolder;
import be.ugent.zeus.hydra.recyclerviewholder.home.SchamperViewHolder;
import be.ugent.zeus.hydra.recyclerviewholder.home.SpecialEventCardViewHolder;

/**
 * Created by feliciaan on 06/04/16.
 */
public class HomeCardAdapter extends RecyclerView.Adapter {

    private List<HomeCard> cardItems;

    public HomeCardAdapter() {
        cardItems = new ArrayList<>();
    }

    /**
     * Remove all items of a given type and a new list
     * @param cardList List with object implementing the card protocol
     * @param type The type of the cards
     */
    public void updateCardItems(List<HomeCard> cardList, HomeType type) {
        Iterator<HomeCard> it = cardItems.iterator();
        while (it.hasNext()) { // Why no filter :(
            HomeCard c = it.next();
            if (c.getCardType() == type) {
                it.remove();
            }
        }

        cardItems.addAll(cardList);

        Collections.sort(cardItems, new Comparator<HomeCard>() {
            @Override
            public int compare(HomeCard lhs, HomeCard rhs) {
                return  -(lhs.getPriority() - rhs.getPriority());
            }
        });

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HomeType type = HomeType.getHomeType(viewType);
        switch (type) {
            case RESTO:
            {
                View v = getViewForLayout(R.layout.home_restocard, parent);
                return new RestoCardViewHolder(v);
            }
            case ACTIVITY:
            {
                View v = getViewForLayout(R.layout.home_activitycard, parent);
                return new ActivityCardViewHolder(v);
            }
            case SPECIALEVENT:
            {
                View v = getViewForLayout(R.layout.home_specialeventcard, parent);
                return new SpecialEventCardViewHolder(v);
            }
            case SCHAMPER:
            {
                View v = getViewForLayout(R.layout.home_schamper_card, parent);
                return new SchamperViewHolder(v);
            }
            case NEWSITEM:
            {
                View v = getViewForLayout(R.layout.home_news_item_card, parent);
                return new NewsItemViewHolder(v);
            }
            case MINERVALOGIN:
            {
                View v = getViewForLayout(R.layout.home_minerva_login_card, parent);
                return new MinervaLoginViewHolder(v);
            }
            case MINERVA_ANNOUNCEMENT:
            {
                throw new IllegalStateException("MINERVA_ANNOUNCEMENT is not yet implemented");
            }

        }
        return null;
    }

    private View getViewForLayout(int rLayout, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(rLayout, parent, false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeCard object = cardItems.get(position);

        AbstractViewHolder vh = (AbstractViewHolder)holder;
        vh.populate(object);
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return cardItems.get(position).getCardType().getType();
    }

    public enum HomeType {
        RESTO(1),
        ACTIVITY(2),
        SPECIALEVENT(3),
        SCHAMPER(4),
        NEWSITEM(5),
        MINERVALOGIN(6),
        MINERVA_ANNOUNCEMENT(7);

        private final int type;

        HomeType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        // Seriously Java, Enum.values() is an expensive operation!
        private static Map<Integer, HomeType> homeTypeMap = new HashMap<>();
        static {
            for (HomeType ht: values()) {
                homeTypeMap.put(ht.getType(), ht);
            }
        }
        public static HomeType getHomeType(int type) {
            return homeTypeMap.get(type);
        }
    }
}
