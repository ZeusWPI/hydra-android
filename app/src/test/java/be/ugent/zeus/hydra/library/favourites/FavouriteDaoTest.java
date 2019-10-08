package be.ugent.zeus.hydra.library.favourites;

import android.content.Context;
import android.util.Pair;
import androidx.annotation.RequiresApi;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
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
import be.ugent.zeus.hydra.feed.cards.database.CardDao;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.testing.Utils;
import okhttp3.internal.Util;
import org.junit.*;
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
public class FavouriteDaoTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private Database database;
    private FavouritesRepository favouritesDao;
    private List<Pair<Library, Boolean>> libraries;

    @Before
    public void setUp() throws IOException {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, Database.class)
                .allowMainThreadQueries()
                .build();
        fillData();
        favouritesDao = database.getFavouritesRepository();
    }

    @After
    public void tearDown() {
        database.clearAllTables();
        database.close();
    }

    private void fillData() throws IOException {
        libraries = new ArrayList<>();
        List<Library> rawData = generate(Library.class, 5, "code", "name")
                .collect(Collectors.toList());
        for (int i = 0; i < rawData.size(); i++) {
            Library library = rawData.get(i);
            library.setCode("test" + i);
            library.setTestName("Lib" + i);
            libraries.add(Pair.create(library, true));
        }

        File sql = Utils.getResourceFile("libraries.sql");
        List<String> inserts = Files.readAllLines(sql.toPath());
        database.runInTransaction(() -> inserts.forEach(s -> database.compileStatement(s).execute()));

        assertEquals("Error during data loading.", libraries.size(), inserts.size());
    }

    @Test
    public void shouldDelete_WhenDeleting() {
        Library deletable = getRandom(libraries).first;
        favouritesDao.delete(deletable);
        assertFalse(favouritesDao.getFavouriteIds().contains(deletable.getCode()));
    }

    @Test
    public void shouldInsert_WhenInsertingNew() {
        Library library = generate(Library.class);
        library.setTestName("New");
        library.setCode("new1");
        favouritesDao.insert(library);

        assertTrue(favouritesDao.getFavouriteIds().contains(library.getCode()));
    }

    @Test
    public void shouldOverwrite_WhenInsertingExisting() {
        Library library = getRandom(libraries).first;
        String old = library.getName();
        library.setTestName("New2");
        favouritesDao.insert(library);

        assertFalse(favouritesDao.getAll().stream()
                .map(LibraryFavourite::getName).collect(Collectors.toList()).contains(old));
        assertTrue(favouritesDao.getAll().stream()
                .map(LibraryFavourite::getName).collect(Collectors.toList()).contains("New2"));
    }
}