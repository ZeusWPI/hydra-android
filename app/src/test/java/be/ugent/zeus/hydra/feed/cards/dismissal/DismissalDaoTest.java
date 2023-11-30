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

package be.ugent.zeus.hydra.feed.cards.dismissal;

import android.content.Context;
import androidx.annotation.RequiresApi;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.testing.Utils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import static be.ugent.zeus.hydra.testing.Assert.assertCollectionEquals;
import static be.ugent.zeus.hydra.testing.Assert.assertThat;
import static be.ugent.zeus.hydra.testing.Assert.samePropertyValuesAs;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static be.ugent.zeus.hydra.testing.Utils.getRandom;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Tests for the {@link DismissalDao}.
 *
 * @author Niko Strijbol
 */
// Request an older version of Android, since the SQLite version in Robolectric does not follow Android releases.
@RequiresApi(api = 26)
@RunWith(AndroidJUnit4.class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = TestApp.class)
@Ignore("Gives weird errors")
public class DismissalDaoTest {

    private Database database;
    private DismissalDao dismissalDao;
    private List<CardDismissal> cards;

    private static Instant p(String d) {
        return Instant.parse(d);
    }

    @Before
    public void setUp() throws IOException {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, Database.class)
                .allowMainThreadQueries()
                .build();
        fillData();
        dismissalDao = database.getCardDao();
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

    @Test
    public void shouldGetOneType_WhenRequestingOneType() {
        List<CardDismissal> expected = cards.stream()
                .filter(c -> c.identifier().getCardType() == Card.Type.RESTO)
                .collect(Collectors.toList());
        List<CardDismissal> actual = dismissalDao.getForType(Card.Type.RESTO);

        assertCollectionEquals(expected, actual);
    }

    @Test
    public void shouldSaveDismissal_WhenInsertingDismissal() {
        CardDismissal dismissal = generate(CardDismissal.class);
        dismissalDao.insert(dismissal);
        List<CardDismissal> dismissals = dismissalDao.getForType(dismissal.identifier().getCardType());
        assertTrue(dismissals.contains(dismissal));
    }

    @Test
    public void shouldSaveDismissal_WhenUpdatingDismissal() {
        CardDismissal random = getRandom(cards);
        CardDismissal update = new CardDismissal(random.identifier(), random.dismissalDate().plusSeconds(60));
        dismissalDao.update(update);
        List<CardDismissal> dismissals = dismissalDao.getForType(random.identifier().getCardType());
        assertTrue(dismissals.contains(update));
        // Find the actual update.
        CardDismissal found = null;
        for (CardDismissal fromDb : dismissals) {
            if (fromDb.equals(update)) {
                found = fromDb;
            }
        }
        assertNotNull(found);
        assertEquals(update, found);
    }

    @Test
    public void shouldDeleteDismissal_WhenDeletingDismissal() {
        CardDismissal dismissal = getRandom(cards);
        dismissalDao.delete(dismissal);
        List<CardDismissal> dismissals = dismissalDao.getForType(dismissal.identifier().getCardType());
        assertFalse(dismissals.contains(dismissal));
    }

    @Test
    public void shouldDeleteAllDismissals_WhenDeletingAllDismissals() {
        dismissalDao.deleteAll();
        // Get the types.
        List<Integer> cardTypes = cards.stream()
                .map(d -> d.identifier().getCardType())
                .distinct()
                .collect(Collectors.toList());
        for (int cardType : cardTypes) {
            assertThat(dismissalDao.getForType(cardType), is(empty()));
        }
    }

    @Test
    public void shouldDeleteDismissalsOfType_WhenDeletingDismissalsOfThoseTypes() {
        List<CardIdentifier> dismissals = getRandom(cards, 2).stream()
                .map(CardDismissal::identifier)
                .collect(Collectors.toList());

        // Get the expected result.
        List<CardIdentifier> expectedType1 = cards.stream()
                .map(CardDismissal::identifier)
                .filter(i -> i.getCardType() == dismissals.get(0).getCardType())
                .filter(i -> !i.equals(dismissals.get(0)))
                .collect(Collectors.toCollection(ArrayList::new));

        List<CardIdentifier> expectedType2 = cards.stream()
                .map(CardDismissal::identifier)
                .filter(i -> i.getCardType() == dismissals.get(1).getCardType())
                .filter(i -> !i.equals(dismissals.get(1)))
                .collect(Collectors.toCollection(ArrayList::new));

        dismissalDao.deleteByIdentifier(dismissals);

        List<CardIdentifier> forType1 = dismissalDao.getForType(dismissals.get(0).getCardType()).stream()
                .map(CardDismissal::identifier)
                .collect(Collectors.toList());
        List<CardIdentifier> forType2 = dismissalDao.getForType(dismissals.get(1).getCardType()).stream()
                .map(CardDismissal::identifier)
                .collect(Collectors.toList());

        assertCollectionEquals(expectedType1, forType1);
        assertCollectionEquals(expectedType2, forType2);
    }
}