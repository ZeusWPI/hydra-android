package be.ugent.zeus.hydra.fragments.home.loader;

import be.ugent.zeus.hydra.models.cards.HomeCard;

import java.util.Comparator;

/**
 * @author Niko Strijbol
 */
public class HomeCardComparator implements Comparator<HomeCard> {

    @Override
    public int compare(HomeCard one, HomeCard other) {
        return -(one.getPriority() - other.getPriority());
    }
}
