package be.ugent.zeus.hydra.feed.cards.database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.feed.cards.CardDismissal;
import be.ugent.zeus.hydra.feed.cards.CardIdentifier;
import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.threeten.bp.Instant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
@RequiresApi(api = 26)
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
public class CardDaoTest {

    private Database database;
    private CardDao cardDao;
    private List<CardDismissal> cards;

    @Before
    public void setUp() throws IOException {
        Context context = RuntimeEnvironment.application;
        database = Room.inMemoryDatabaseBuilder(context, Database.class)
                .allowMainThreadQueries()
                .build();
        fillData();
        cardDao = database.getCardDao();
    }

    @SuppressWarnings("WrongConstant")
    private void fillData() throws IOException {
        cards = new ArrayList<>();
        cards.add(new CardDismissal(new CardIdentifier(1, "test1"), p("2064-06-25T20:30:24Z")));
        cards.add(new CardDismissal(new CardIdentifier(1, "test2"), p("2067-02-28T21:00:35Z")));
        cards.add(new CardDismissal(new CardIdentifier(1, "test3"), p("1980-04-05T07:18:15Z")));
        cards.add(new CardDismissal(new CardIdentifier(2, "test1"), p("2010-03-06T21:01:04Z")));
        cards.add(new CardDismissal(new CardIdentifier(3, "test4"), p("2061-07-28T12:03:37Z")));
        cards.add(new CardDismissal(new CardIdentifier(4, "test1"), p("2026-11-07T07:23:49Z")));

        File sql = Utils.getResourceFile("feed/dismissals.sql");
        List<String> inserts = Files.readAllLines(sql.toPath());
        inserts.forEach(s -> database.compileStatement(s).execute());

        assertEquals("Error during data loading.", cards.size(), inserts.size());
    }

    private static Instant p(String d) {
        return Instant.parse(d);
    }

    @Test
    @SuppressWarnings("WrongConstant")
    public void testGetForType() {
        List<CardDismissal> expected = cards.stream()
                .filter(c -> c.getIdentifier().getCardType() == 1)
                .collect(Collectors.toList());
        List<CardDismissal> actual = cardDao.getForType(1);

        assertCollectionEquals(expected, actual);
    }

    @Test
    @SuppressWarnings("WrongConstant")
    public void testGetIdForType() {
        List<CardIdentifier> expected = cards.stream()
                .filter(c -> c.getIdentifier().getCardType() == 1)
                .map(CardDismissal::getIdentifier)
                .collect(Collectors.toList());
        List<CardIdentifier> actual = cardDao.getIdsForType(1);

        assertCollectionEquals(expected, actual);
    }

    @Test
    public void testInsert() {
        CardDismissal dismissal = generate(CardDismissal.class);
        cardDao.insert(dismissal);
        List<CardDismissal> dismissals = cardDao.getForType(dismissal.getIdentifier().getCardType());
        assertTrue(dismissals.contains(dismissal));
    }

    @Test
    public void testUpdate() {
        CardDismissal random = getRandom(cards);
        CardDismissal update = new CardDismissal(random.getIdentifier(), random.getDismissalDate().plusSeconds(60));
        cardDao.update(update);
        List<CardDismissal> dismissals = cardDao.getForType(random.getIdentifier().getCardType());
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
        cardDao.delete(dismissal);
        List<CardDismissal> dismissals = cardDao.getForType(dismissal.getIdentifier().getCardType());
        assertFalse(dismissals.contains(dismissal));
    }

    @Test
    public void testDeleteAll() {
        cardDao.deleteAll();
        // Get the types.
        List<Integer> cardTypes = cards.stream()
                .map(d -> d.getIdentifier().getCardType())
                .distinct()
                .collect(Collectors.toList());
        for (int cardType: cardTypes) {
            assertTrue(cardDao.getForType(cardType).isEmpty());
        }
    }

    @Test
    public void testDeleteByIdentifier() {
        List<CardIdentifier> dismissals = getRandom(cards, 2).stream()
                .map(CardDismissal::getIdentifier)
                .collect(Collectors.toList());

        // Get the expected result.
        List<CardIdentifier> expectedType1 = cards.stream()
                .map(CardDismissal::getIdentifier)
                .filter(i -> i.getCardType() == dismissals.get(0).getCardType())
                .collect(Collectors.toCollection(ArrayList::new));

        List<CardIdentifier> expectedType2 = cards.stream()
                .map(CardDismissal::getIdentifier)
                .filter(i -> i.getCardType() == dismissals.get(1).getCardType())
                .collect(Collectors.toCollection(ArrayList::new));

        cardDao.deleteByIdentifier(dismissals);

        List<CardIdentifier> forType1 = cardDao.getForType(dismissals.get(0).getCardType()).stream()
                .map(CardDismissal::getIdentifier)
                .collect(Collectors.toList());
        List<CardIdentifier> forType2 = cardDao.getForType(dismissals.get(1).getCardType()).stream()
                .map(CardDismissal::getIdentifier)
                .collect(Collectors.toList());

        assertCollectionEquals(expectedType1, forType1);
        assertCollectionEquals(expectedType2, forType2);
    }
}