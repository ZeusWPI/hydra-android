package be.ugent.zeus.hydra.feed.cards.implementations.library;

import android.util.Pair;

import java.util.List;

import java9.util.Optional;

import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.library.details.OpeningHours;

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

    private final List<Pair<String, Result<Optional<OpeningHours>>>> libraries;

    LibraryCard(List<Pair<String, Result<Optional<OpeningHours>>>> libraries) {
        this.libraries = libraries;
    }

    public List<Pair<String, Result<Optional<OpeningHours>>>> getLibraries() {
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
        return LibraryCard.class.hashCode();
    }
}