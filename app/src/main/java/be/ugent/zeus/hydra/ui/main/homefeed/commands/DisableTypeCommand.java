package be.ugent.zeus.hydra.ui.main.homefeed.commands;

import android.content.Context;
import android.support.annotation.StringRes;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.domain.models.feed.Card;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedFragment;
import be.ugent.zeus.hydra.utils.PreferencesUtils;

/**
 * This will add a certain {@link Card.Type} to the list of hidden
 * types.
 *
 * @author Niko Strijbol
 */
public class DisableTypeCommand implements FeedCommand {

    @Card.Type
    private final int cardType;

    public DisableTypeCommand(@Card.Type int cardType) {
        this.cardType = cardType;
    }

    @Override
    @Card.Type
    public int execute(Context context) {
        PreferencesUtils.addToStringSet(
                context,
                HomeFeedFragment.PREF_DISABLED_CARD_TYPES,
                String.valueOf(cardType)
        );
        return cardType;
    }

    @Override
    @Card.Type
    public int undo(Context context) {
        PreferencesUtils.removeFromStringSet(
                context,
                HomeFeedFragment.PREF_DISABLED_CARD_TYPES,
                String.valueOf(cardType)
        );
        return cardType;
    }

    @Override
    @StringRes
    public int getCompleteMessage() {
        return R.string.home_feed_card_type_hidden;
    }

    @Override
    @StringRes
    public int getUndoMessage() {
        return R.string.home_feed_undone;
    }
}