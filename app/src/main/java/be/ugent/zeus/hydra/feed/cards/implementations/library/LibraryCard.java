package be.ugent.zeus.hydra.feed.cards.implementations.library;

import android.util.Pair;

import java.util.List;

import java9.util.Objects;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.feed.cards.implementations.urgent.UrgentCard;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.details.OpeningHours;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoMenu;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

/**
 * A card that will display the opening hours of libraries that are in the user's favourites.
 *
 * @author Niko Strijbol
 */
class LibraryCard extends Card {

    /**
     * This is used in the database; do not rename.
     */
    private static final String TAG = "LibraryCard";

    private final List<Pair<Library, OpeningHours>> libraries;

    LibraryCard(List<Pair<Library, OpeningHours>> libraries) {
        this.libraries = libraries;
    }

    public List<Pair<Library, OpeningHours>> getLibraries() {
        return libraries;
    }

    @Override
    public int getPriority() {
        return PriorityUtils.FEED_SPECIAL_SHIFT + 1;
    }

    @Override
    public String getIdentifier() {
        return TAG;
    }

    @Override
    public int getCardType() {
        return Type.LIBRARY;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LibraryCard;
    }

    @Override
    public int hashCode() {
        return UrgentCard.class.hashCode();
    }
}