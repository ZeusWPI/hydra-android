package be.ugent.zeus.hydra.feed.commands;

import android.content.Context;
import androidx.annotation.StringRes;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.utils.PreferencesUtils;
import be.ugent.zeus.hydra.feed.HomeFeedFragment;
import be.ugent.zeus.hydra.feed.cards.Card;

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
        Reporting.getTracker(context).log(new DismissalEvent(cardType));
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
        return R.string.feed_card_hidden_type;
    }
}
