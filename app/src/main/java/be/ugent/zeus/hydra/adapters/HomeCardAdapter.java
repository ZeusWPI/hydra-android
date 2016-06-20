package be.ugent.zeus.hydra.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerviewholder.home.AbstractViewHolder;
import be.ugent.zeus.hydra.recyclerviewholder.home.ActivityCardViewHolder;
import be.ugent.zeus.hydra.recyclerviewholder.home.RestoCardViewHolder;
import be.ugent.zeus.hydra.recyclerviewholder.home.SchamperViewHolder;
import be.ugent.zeus.hydra.recyclerviewholder.home.SpecialEventCardViewHolder;

import java.util.*;

import static be.ugent.zeus.hydra.models.cards.HomeCard.CardType.*;

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
    public void updateCardItems(List<HomeCard> cardList, @HomeCard.CardType int type) {
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
        switch (viewType) {
            case RESTO:
                return new RestoCardViewHolder(getViewForLayout(R.layout.home_card_resto, parent));
            case ACTIVITY:
                return new ActivityCardViewHolder(getViewForLayout(R.layout.home_card_event, parent));
            case SPECIAL_EVENT:
                return new SpecialEventCardViewHolder(getViewForLayout(R.layout.home_card_special, parent));
            case SCHAMPER:
                return new SchamperViewHolder(getViewForLayout(R.layout.card_schamper, parent));
        }
        return null;
    }

    private View getViewForLayout(int rLayout, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(rLayout, parent, false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeCard object = cardItems.get(position);

        AbstractViewHolder vh = (AbstractViewHolder) holder;
        vh.populate(object);
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
}