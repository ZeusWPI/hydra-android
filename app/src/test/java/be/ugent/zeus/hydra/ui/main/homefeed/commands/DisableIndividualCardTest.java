package be.ugent.zeus.hydra.ui.main.homefeed.commands;

import android.content.Context;

import be.ugent.zeus.hydra.feed.Card;
import be.ugent.zeus.hydra.feed.CardDismissal;
import be.ugent.zeus.hydra.feed.CardIdentifier;
import be.ugent.zeus.hydra.feed.CardRepository;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
public class DisableIndividualCardTest {

    @Test
    public void testExecuteAndUndo() {
        // TODO: this relies a bit much on the internals.
        CardRepository repository = new MemoryCardRepository();
        CardDismissal cardDismissal = mock(CardDismissal.class);
        CardIdentifier identifier = mock(CardIdentifier.class);
        when(cardDismissal.getIdentifier()).thenReturn(identifier);
        when(identifier.getCardType()).thenReturn(Card.Type.DEBUG);
        when(identifier.getIdentifier()).thenReturn("test");
        Context context = mock(Context.class);

        DisableIndividualCard disableIndividualCard = new DisableIndividualCard(cardDismissal, c -> repository);
        disableIndividualCard.execute(context);

        List<CardDismissal> dismissals = repository.getForType(Card.Type.DEBUG);
        assertEquals(1, dismissals.size());
        assertEquals(identifier.getIdentifier(), dismissals.get(0).getIdentifier().getIdentifier());

        disableIndividualCard.undo(context);

        List<CardDismissal> newDismissals = repository.getForType(Card.Type.DEBUG);
        assertTrue(newDismissals.isEmpty());
    }
}