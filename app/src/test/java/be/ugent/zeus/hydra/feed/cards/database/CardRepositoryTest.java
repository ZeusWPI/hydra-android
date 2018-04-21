package be.ugent.zeus.hydra.feed.cards.database;

import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.feed.cards.CardDismissal;
import be.ugent.zeus.hydra.feed.cards.CardIdentifier;
import be.ugent.zeus.hydra.feed.cards.CardRepository;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.Assert.*;
import static be.ugent.zeus.hydra.testing.Assert.assertThat;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static be.ugent.zeus.hydra.testing.Utils.getRandom;
import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class CardRepositoryTest {

    private CardDao dao;
    private List<CardDismissal> cards;
    private CardRepository repository;

    @Before
    @SuppressWarnings("WrongConstant")
    public void setUp() {
        cards = new ArrayList<>();
        cards.add(new CardDismissal(new CardIdentifier(1, "test1"), p("2064-06-25T20:30:24Z")));
        cards.add(new CardDismissal(new CardIdentifier(1, "test2"), p("2067-02-28T21:00:35Z")));
        cards.add(new CardDismissal(new CardIdentifier(1, "test3"), p("1980-04-05T07:18:15Z")));
        cards.add(new CardDismissal(new CardIdentifier(2, "test1"), p("2010-03-06T21:01:04Z")));
        cards.add(new CardDismissal(new CardIdentifier(3, "test4"), p("2061-07-28T12:03:37Z")));
        cards.add(new CardDismissal(new CardIdentifier(4, "test1"), p("2026-11-07T07:23:49Z")));

        dao = new MemoryCardDao(cards);
        repository = new CardDatabaseRepository(dao);
    }

    private Instant p(String d) {
        return Instant.parse(d);
    }

    @Test
    @SuppressWarnings("WrongConstant")
    public void testGetForType() {
        List<CardDismissal> expected = dao.getForType(1);
        List<CardDismissal> actual = repository.getForType(1);
        assertCollectionEquals(expected, actual);
    }

    @Test
    @SuppressWarnings("WrongConstant")
    public void testGetIdForType() {
        List<CardIdentifier> expected = dao.getIdsForType(1);
        List<CardIdentifier> actual = repository.getIdForType(1);
        assertCollectionEquals(expected, actual);
    }

    @Test
    public void testInsert() {
        CardDismissal dismissal = generate(CardDismissal.class);
        repository.add(dismissal);
        List<CardDismissal> dismissals = dao.getForType(dismissal.getIdentifier().getCardType());
        assertTrue(dismissals.contains(dismissal));
    }

    @Test
    @SuppressWarnings("Duplicates")
    public void testUpdate() {
        CardDismissal random = getRandom(cards);
        CardDismissal update = new CardDismissal(random.getIdentifier(), random.getDismissalDate().plusSeconds(60));
        repository.update(update);
        List<CardDismissal> dismissals = dao.getForType(random.getIdentifier().getCardType());
        assertTrue(dismissals.contains(update));
        // Find the actual update.
        CardDismissal found = null;
        for (CardDismissal fromDb: dismissals) {
            if (fromDb.equals(update)) {
                found = fromDb;
            }
        }
        assertNotNull(found);
        assertThat(update, samePropertyValuesAs(found));
    }

    @Test
    public void testDelete() {
        CardDismissal dismissal = getRandom(cards);
        repository.delete(dismissal);
        List<CardDismissal> dismissals = dao.getForType(dismissal.getIdentifier().getCardType());
        assertFalse(dismissals.contains(dismissal));
    }

    @Test
    public void testDeleteAll() {
        repository.deleteAll();
        // Get the types.
        List<Integer> cardTypes = cards.stream()
                .map(d -> d.getIdentifier().getCardType())
                .distinct()
                .collect(Collectors.toList());
        for (int cardType: cardTypes) {
            assertTrue(dao.getForType(cardType).isEmpty());
        }
    }
}