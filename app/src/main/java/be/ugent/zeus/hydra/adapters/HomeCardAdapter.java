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
import be.ugent.zeus.hydra.models.HomeCard;
import be.ugent.zeus.hydra.recyclerviewholder.home.AbstractViewHolder;
import be.ugent.zeus.hydra.recyclerviewholder.home.ActivityCardViewHolder;
import be.ugent.zeus.hydra.recyclerviewholder.home.RestoCardViewHolder;

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

        for (HomeCard card: cardList) {
            System.out.println("" + card + ": " + card.getPriority());
        }

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
                View v = getViewForLayout(R.layout.activity_listitem, parent);
                return new ActivityCardViewHolder(v);
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
        ACTIVITY(2);

        private final int type;

        private HomeType(int type) {
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
