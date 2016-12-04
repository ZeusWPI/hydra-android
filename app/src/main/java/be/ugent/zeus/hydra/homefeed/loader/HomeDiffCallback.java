package be.ugent.zeus.hydra.homefeed.loader;

import android.support.v7.util.DiffUtil;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;

import java.util.List;

/**
 * Calculate diffs for the home cards.
 *
 * Items are the same if the have the same {@link HomeCard.CardType}.
 * Items have the same contents if the {@link HomeCard#equals(Object)} method returns true.
 *
 * @author Niko Strijbol
 */
public class HomeDiffCallback extends DiffUtil.Callback {

    private List<HomeCard> oldList;
    private List<HomeCard> newList;

    public HomeDiffCallback(List<HomeCard> oldStream, List<HomeCard> newStream) {
        oldList = oldStream;
        newList = newStream;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getCardType() == newList.get(newItemPosition).getCardType();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}