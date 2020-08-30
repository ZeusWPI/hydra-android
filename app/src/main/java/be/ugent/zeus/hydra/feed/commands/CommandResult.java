package be.ugent.zeus.hydra.feed.commands;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import be.ugent.zeus.hydra.feed.cards.Card;

/**
 * @author Niko Strijbol
 */
public class CommandResult {

    @Card.Type
    private final int cardType;
    private final FeedCommand command;

    private CommandResult(@Card.Type int cardType, @Nullable FeedCommand command) {
        this.cardType = cardType;
        this.command = command;
    }

    public static CommandResult forUndo(@Card.Type int cardType) {
        return new CommandResult(cardType, null);
    }

    public static CommandResult forExecute(FeedCommand command, @Card.Type int cardType) {
        return new CommandResult(cardType, command);
    }

    @Card.Type
    public int getCardType() {
        return cardType;
    }

    public boolean wasUndo() {
        return command == null;
    }

    @NonNull
    public FeedCommand getCommand() {
        //noinspection ConstantConditions
        return Objects.requireNonNull(command);
    }
}