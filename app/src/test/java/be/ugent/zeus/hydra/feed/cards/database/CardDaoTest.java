package be.ugent.zeus.hydra.feed.cards.database;

import android.content.Context;

import androidx.annotation.RequiresApi;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardDismissal;
import be.ugent.zeus.hydra.feed.cards.CardIdentifier;
import be.ugent.zeus.hydra.testing.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import org.threeten.bp.Instant;

import static be.ugent.zeus.hydra.testing.Assert.assertThat;
import static be.ugent.zeus.hydra.testing.Assert.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static be.ugent.zeus.hydra.testing.Utils.getRandom;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Tests for the {@link CardDao}.
 *
 * @author Niko Strijbol
 */
// Request an older version of Android, since the SQLite version in Robolectric does not follow Android releases.
@RequiresApi(api = 26)
@RunWith(AndroidJUnit4.class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = TestApp.class)
public class CardDaoTest {

    private Database database;
    private CardDao cardDao;
    private List<CardDismissal> cards;

    @Before
    public void setUp() throws IOException {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, Database.class)
                .allowMainThreadQueries()
                .build();
        fillData();
        cardDao = database.getCardDao();
    }

    @After
    public void tearDown() {
        database.clearAllTables();
        database.close();
    }

    private void fillData() throws IOException {
        cards = new ArrayList<>();
        cards.add(new CardDismissal(new CardIdentifier(Card.Type.RESTO, "test1"), p("2064-06-25T20:30:24Z")));
        cards.add(new CardDismissal(new CardIdentifier(Card.Type.RESTO, "test2"), p("2067-02-28T21:00:35Z")));
        cards.add(new CardDismissal(new CardIdentifier(Card.Type.RESTO, "test3"), p("1980-04-05T07:18:15Z")));
        cards.add(new CardDismissal(new CardIdentifier(Card.Type.ACTIVITY, "test1"), p("2010-03-06T21:01:04Z")));
        cards.add(new CardDismissal(new CardIdentifier(Card.Type.SPECIAL_EVENT, "test4"), p("2061-07-28T12:03:37Z")));
        cards.add(new CardDismissal(new CardIdentifier(Card.Type.NEWS_ITEM, "test1"), p("2026-11-07T07:23:49Z")));

        File sql = Utils.getResourceFile("feed/dismissals.sql");
        List<String> inserts = Files.readAllLines(sql.toPath());
        database.runInTransaction(() -> inserts.forEach(s -> database.compileStatement(s).execute()));

        assertEquals("Error during data loading.", cards.size(), inserts.size());
    }

    private static Instant p(String d) {
        return Instant.parse(d);
    }

    @Test
    public void shouldGetOneType_WhenRequestingOneType() {
        List<CardDismissal> expected = cards.stream()
                .filter(c -> c.getIdentifier().getCardType() == Card.Type.RESTO)
                .collect(Collectors.toList());
        List<CardDismissal> actual = cardDao.getForType(Card.Type.RESTO);

        assertCollectionEquals(expected, actual);
    }

    @Test
    public void shouldSaveDismissal_WhenInsertingDismissal() {
        CardDismissal dismissal = generate(CardDismissal.class);
        cardDao.insert(dismissal);
        List<CardDismissal> dismissals = cardDao.getForType(dismissal.getIdentifier().getCardType());
        assertTrue(dismissals.contains(dismissal));
    }

    @Test
    public void shouldSaveDismissal_WhenUpdatingDismissal() {
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
    public void shouldDeleteDismissal_WhenDeletingDismissal() {
        CardDismissal dismissal = getRandom(cards);
        cardDao.delete(dismissal);
        List<CardDismissal> dismissals = cardDao.getForType(dismissal.getIdentifier().getCardType());
        assertFalse(dismissals.contains(dismissal));
    }

    @Test
    public void shouldDeleteAllDismissals_WhenDeletingAllDismissals() {
        cardDao.deleteAll();
        // Get the types.
        List<Integer> cardTypes = cards.stream()
                .map(d -> d.getIdentifier().getCardType())
                .distinct()
                .collect(Collectors.toList());
        for (int cardType: cardTypes) {
            assertThat(cardDao.getForType(cardType), is(empty()));
        }
    }

    @Test
    public void shouldDeleteDismissalsOfType_WhenDeletingDismissalsOfThoseTypes() {
        List<CardIdentifier> dismissals = getRandom(cards, 2).stream()
                .map(CardDismissal::getIdentifier)
                .collect(Collectors.toList());

        // Get the expected result.
        List<CardIdentifier> expectedType1 = cards.stream()
                .map(CardDismissal::getIdentifier)
                .filter(i -> i.getCardType() == dismissals.get(0).getCardType())
                .filter(i -> !i.equals(dismissals.get(0)))
                .collect(Collectors.toCollection(ArrayList::new));

        List<CardIdentifier> expectedType2 = cards.stream()
                .map(CardDismissal::getIdentifier)
                .filter(i -> i.getCardType() == dismissals.get(1).getCardType())
                .filter(i -> !i.equals(dismissals.get(1)))
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