package be.ugent.zeus.hydra.feed.commands;

import android.content.Context;

import java9.util.function.Function;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.CardDismissal;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;

/**
 * @author Niko Strijbol
 */
public class DisableIndividualCard implements FeedCommand {

    private final CardDismissal cardDismissal;
    private final Function<Context, DismissalDao> daoSupplier;

    public DisableIndividualCard(Card card) {
        this(CardDismissal.dismiss(card), c -> Database.get(c).getCardDao());
    }

    public DisableIndividualCard(CardDismissal card, Function<Context, DismissalDao> daoSupplier) {
        this.cardDismissal = card;
        this.daoSupplier = daoSupplier;
    }

    @Override
    public int execute(Context context) {
        DismissalDao dao = daoSupplier.apply(context);
        dao.insert(cardDismissal);
        return cardDismissal.getIdentifier().getCardType();
    }

    @Override
    public int undo(Context context) {
        DismissalDao dao = daoSupplier.apply(context);
        dao.delete(cardDismissal);
        return cardDismissal.getIdentifier().getCardType();
    }

    @Override
    public int getCompleteMessage() {
        return R.string.feed_card_hidden_single;
    }
}
