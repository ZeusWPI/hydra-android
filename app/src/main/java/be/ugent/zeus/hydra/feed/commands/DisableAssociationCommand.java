package be.ugent.zeus.hydra.feed.commands;

import android.content.Context;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.association.preference.AssociationSelectPrefActivity;
import be.ugent.zeus.hydra.utils.PreferencesUtils;

/**
 * @author Niko Strijbol
 */
public class DisableAssociationCommand implements FeedCommand {

    private final Association association;

    public DisableAssociationCommand(Association association) {
        this.association = association;
    }

    @Override
    public int execute(Context context) {
        PreferencesUtils.addToStringSet(
                context,
                AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING,
                association.getInternalName()
        );
        return Card.Type.ACTIVITY;
    }

    @Override
    public int undo(Context context) {
        PreferencesUtils.removeFromStringSet(
                context,
                AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING,
                association.getInternalName()
        );
        return Card.Type.ACTIVITY;
    }

    @Override
    public int getCompleteMessage() {
        return R.string.home_feed_association_hidden;
    }

    @Override
    public int getUndoMessage() {
        return R.string.home_feed_undone;
    }
}