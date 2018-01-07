package be.ugent.zeus.hydra.ui.main.homefeed.commands;

import android.content.Context;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.database.RepositoryFactory;
import be.ugent.zeus.hydra.domain.models.feed.Card;
import be.ugent.zeus.hydra.domain.models.feed.CardDismissal;
import be.ugent.zeus.hydra.domain.repository.CardRepository;
import java8.util.function.Function;

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
        return R.string.home_feed_card_done;
    }

    @Override
    public int getUndoMessage() {
        return R.string.home_feed_undone;
    }
}
