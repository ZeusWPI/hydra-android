package be.ugent.zeus.hydra.feed.commands;

import android.content.Context;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.database.RepositoryFactory;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardDismissal;
import be.ugent.zeus.hydra.feed.cards.CardRepository;
import java9.util.function.Function;

/**
 * @author Niko Strijbol
 */
public class DisableIndividualCard implements FeedCommand {

    private final CardDismissal cardDismissal;
    private final Function<Context, CardRepository> repositorySupplier;

    public DisableIndividualCard(Card card) {
        this(CardDismissal.dismiss(card), RepositoryFactory::getCardRepository);
    }

    public DisableIndividualCard(CardDismissal card, Function<Context, CardRepository> repositorySupplier) {
        this.cardDismissal = card;
        this.repositorySupplier = repositorySupplier;
    }

    @Override
    public int execute(Context context) {
        CardRepository repository = repositorySupplier.apply(context);
        repository.add(cardDismissal);
        return cardDismissal.getIdentifier().getCardType();
    }

    @Override
    public int undo(Context context) {
        CardRepository repository = repositorySupplier.apply(context);
        repository.delete(cardDismissal);
        return cardDismissal.getIdentifier().getCardType();
    }

    @Override
    public int getCompleteMessage() {
        return R.string.feed_card_hidden_single;
    }
}
