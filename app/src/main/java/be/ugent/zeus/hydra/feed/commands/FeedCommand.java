package be.ugent.zeus.hydra.feed.commands;

import android.content.Context;
import androidx.annotation.StringRes;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.Card;

/**
 * Execute a command. Afterwards the cards of the returned type will be refreshed.
 * <p>
 * A command is normally one-use: calling it multiple times is undefined behaviour. Additionally, a command should
 * be a fairly short operation.
 *
 * @author Niko Strijbol
 */
public interface FeedCommand {

    /**
     * Execute the command.
     *
     * @return Returns the type of card that should be refreshed.
     */
    @Card.Type
    int execute(Context context);

    /**
     * Undo the command if possible. If the command has not been executed yet, this will result in undefined behaviour.
     *
     * @return The affected card type.
     */
    @Card.Type
    int undo(Context context);

    /**
     * The message to be shown when the command has successfully completed.
     *
     * @return The string resource.
     */
    @StringRes
    int getCompleteMessage();

    /**
     * The message to be shown when the command has successfully undone.
     *
     * @return The string resource.
     */
    @StringRes
    default int getUndoMessage() {
        return R.string.action_undo;
    }
}