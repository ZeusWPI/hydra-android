/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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