package be.ugent.zeus.hydra.common;

import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static be.ugent.zeus.hydra.testing.Assert.assertCollectionEquals;
import static be.ugent.zeus.hydra.testing.Utils.getRandom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = 26)
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
public abstract class FullRepositoryTest<ID, M> {

    /**
     * @return The repository to test. Can be called multiple times.
     */
    protected abstract FullRepository<ID, M> getRepository();

    /**
     * @return The backing data.
     */
    protected abstract List<M> getData();

    /**
     * @return Function to extract the id.
     */
    protected abstract Function<M, ID> getIdExtractor();

    /**
     * Execute a deep equals, thus not solely relying on the equals method. Should probably call assertThat(actual,
     * samePropertyValuesAs(expected));
     *
     * @param expected The expected object.
     * @param actual   The actual object.
     */
    protected abstract void assertDeepEquals(M expected, M actual);

    protected abstract M generateRandom();

    protected abstract List<M> generateRandom(int amount);

    protected abstract M generateRandomUpdateFor(M other);

    protected abstract List<M> generateRandomUpdateFor(List<M> other);

    @Test
    public void getOne() {
        M expected = getRandom(getData());
        M actual = getRepository().getOne(getIdExtractor().apply(expected));
        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }

    @Test
    public void getAll() {
        List<M> expected = getData();
        List<M> actual = getRepository().getAll();
        assertCollectionEquals(expected, actual);
        // Also
        for (int i = 0; i < expected.size(); i++) {
            assertDeepEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void insertOne() {
        M instance = generateRandom();
        getRepository().insert(instance);
        M actual = getRepository().getOne(getIdExtractor().apply(instance));
        assertEquals(instance, actual);
        assertDeepEquals(instance, actual);
    }

    @Test
    public void insertCollection() {
        final int NR_OF_ITEMS = 5;
        List<M> expected = generateRandom(NR_OF_ITEMS);
        getRepository().insert(expected);
        for (M e : expected) {
            M actual = getRepository().getOne(getIdExtractor().apply(e));
            assertEquals(e, actual);
            assertDeepEquals(e, actual);
        }
    }

    @Test
    public void updateOne() {
        M originalItem = getRandom(getData());
        M updatedItem = generateRandomUpdateFor(originalItem);

        getRepository().update(updatedItem);

        M actual = getRepository().getOne(getIdExtractor().apply(updatedItem));
        assertEquals(updatedItem, actual);
        assertDeepEquals(updatedItem, actual);
    }

    @Test
    public void updateCollection() {
        final int NR_OF_ITEMS = 5;
        List<M> originalItems = getRandom(getData(), NR_OF_ITEMS);
        List<M> updatedItems = generateRandomUpdateFor(originalItems);

        getRepository().update(updatedItems);

        for (int i = 0; i < NR_OF_ITEMS; i++) {
            M actual = getRepository().getOne(getIdExtractor().apply(updatedItems.get(i)));
            assertEquals(updatedItems.get(i), actual);
            assertDeepEquals(updatedItems.get(i), actual);
        }
    }

    @Test
    public void deleteOne() {
        M original = getRandom(getData());
        getRepository().delete(original);
        List<M> items = getRepository().getAll();
        assertEquals(getData().size() - 1, items.size());
        assertFalse(items.contains(original));
    }

    @Test
    public void deleteCollection() {
        final int NR_OF_ITEMS = 5;
        List<M> originals = getRandom(getData(), NR_OF_ITEMS);
        getRepository().delete(originals);
        List<M> items = getRepository().getAll();
        assertEquals(getData().size() - NR_OF_ITEMS, items.size());
        for (M original : originals) {
            assertFalse(items.contains(original));
        }
    }

    @Test
    public void deleteOneById() {
        M original = getRandom(getData());
        getRepository().deleteById(getIdExtractor().apply(original));
        List<M> items = getRepository().getAll();
        assertEquals(getData().size() - 1, items.size());
        assertFalse(items.contains(original));
    }

    @Test
    public void deleteCollectionById() {
        final int NR_OF_ITEMS = 5;
        List<M> originals = getRandom(getData(), NR_OF_ITEMS);
        getRepository().deleteById(originals.stream().map(getIdExtractor()).collect(Collectors.toList()));
        List<M> items = getRepository().getAll();
        assertEquals(getData().size() - NR_OF_ITEMS, items.size());
        for (M original : originals) {
            assertFalse(items.contains(original));
        }
    }

    @Test
    public void deleteAll() {
        getRepository().deleteAll();
        assertTrue(getRepository().getAll().isEmpty());
    }
}