package be.ugent.zeus.hydra.ui.main.homefeed.commands;

import android.content.Context;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.utils.PreferencesUtils;

/**
 * @author Niko Strijbol
 */
public class DisableIndividualCard implements FeedCommand {

    private final String id;
    private final String key;
    @HomeCard.CardType
    private final int cardType;

    public DisableIndividualCard(String key, String id, @HomeCard.CardType int cardType) {
        this.id = id;
        this.key = key;
        this.cardType = cardType;
    }

    @Override
    public int execute(Context context) {
        PreferencesUtils.addToStringSet(context, key, id);
        return cardType;
    }

    @Override
    public int undo(Context context) {
        PreferencesUtils.removeFromStringSet(context, key, id);
        return cardType;
    }

    @Override
    public int getCompleteMessage() {
        return R.string.home_feed_card_done;
    }

    @Override
    public int getUndoMessage() {
        return R.string.home_feed_undone;
    }
}
