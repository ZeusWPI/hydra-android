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
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.testing.Utils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import static be.ugent.zeus.hydra.testing.Utils.generate;
import static be.ugent.zeus.hydra.testing.Utils.getRandom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
public class FavouriteDaoTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    private Database database;
    private FavouriteRepository favouritesDao;
    private List<Pair<Library, Boolean>> libraries;

    @Before
    public void setUp() throws IOException {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, Database.class)
                .allowMainThreadQueries()
                .build();
        fillData();
        favouritesDao = database.getFavouriteRepository();
    }

    @After
    public void tearDown() {
        database.clearAllTables();
        database.close();
    }

    private void fillData() throws IOException {
        libraries = new ArrayList<>();
        var rawData = generate(Library.class, 5).toList();
        for (int i = 0; i < rawData.size(); i++) {
            Library library = rawData.get(i)
                    .withCode("test" + i)
                    .withName("Lib" + i);
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
        assertFalse(favouritesDao.getFavouriteIds().contains(deletable.code()));
    }

    @Test
    public void shouldInsert_WhenInsertingNew() {
        Library library = generate(Library.class);
        favouritesDao.insert(library);

        assertTrue(favouritesDao.getFavouriteIds().contains(library.code()));
    }

    @Test
    public void shouldOverwrite_WhenInsertingExisting() {
        Library library = getRandom(libraries).first;
        String old = library.name();
        var newLib = library.withName("New2");
        favouritesDao.insert(newLib);

        assertFalse(favouritesDao.getAll().stream()
                .map(LibraryFavourite::getName).collect(Collectors.toList()).contains(old));
        assertTrue(favouritesDao.getAll().stream()
                .map(LibraryFavourite::getName).collect(Collectors.toList()).contains("New2"));
    }
}