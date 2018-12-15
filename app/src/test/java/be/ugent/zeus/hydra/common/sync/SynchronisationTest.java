package be.ugent.zeus.hydra.common.sync;

import be.ugent.zeus.hydra.common.FullRepository;
import java9.util.function.Function;
import org.junit.Test;

import java.util.*;

import static be.ugent.zeus.hydra.testing.Assert.assertCollectionEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Niko Strijbol
 */
public class SynchronisationTest {

    @Test
    public void classify() {

        Collection<Integer> newData = Arrays.asList(1, 2, 3, 4, 5);
        Collection<Integer> oldData = Arrays.asList(2, 63, 3, 4, 5, 65, 100, 500, 0);

        Synchronisation<Integer, Integer> synchronisation = new Synchronisation<>(oldData, newData, Function.identity());
        Synchronisation.Diff<Integer, Integer> diff = synchronisation.diff();

        Collection<Integer> expectedNew = Collections.singleton(1);
        Collection<Integer> expectedStale = Arrays.asList(63, 65, 100, 500, 0);
        Collection<Integer> expectedUpdated = Arrays.asList(2, 3, 4, 5);

        assertCollectionEquals(expectedNew, diff.getNew());
        assertCollectionEquals(expectedStale, diff.getStaleIds());
        assertCollectionEquals(expectedUpdated, diff.getUpdated());
    }

    @Test
    public void classifyEmpty() {

        Collection<Integer> newData = Collections.emptySet();
        Collection<Integer> oldData = new HashSet<>(Arrays.asList(2, 63, 3, 4, 5, 65, 100, 5000, 0));

        Synchronisation<Integer, Integer> synchronisation = new Synchronisation<>(oldData, newData, Function.identity());
        Synchronisation.Diff<Integer, Integer> diff = synchronisation.diff();

        assertCollectionEquals(Collections.emptyList(), diff.getNew());
        assertCollectionEquals(oldData, diff.getStaleIds());
        assertCollectionEquals(Collections.emptyList(), diff.getUpdated());
    }

    private static class TestObject {
        final String id;

        private TestObject(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    @Test
    public void classifyObjects() {

        Set<TestObject> newData = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            newData.add(new TestObject("ID" + i));
        }

        Set<String> oldData = new HashSet<>();
        for (int i = -5; i < 20; i++) {
            oldData.add("ID" + i);
        }

        Synchronisation<TestObject, String> synchronisation = new Synchronisation<>(
                oldData,
                newData,
                o -> o.id
        );
        Synchronisation.Diff<TestObject, String> diff = synchronisation.diff();

        Set<TestObject> expectedUpdates = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            expectedUpdates.add(new TestObject("ID" + i));
        }
        assertCollectionEquals(expectedUpdates, diff.getUpdated()); // Test updated ids.

        Set<TestObject> expectedNew = new HashSet<>();
        for (int i = 20; i < 50; i++) {
            expectedNew.add(new TestObject("ID" + i));
        }
        assertCollectionEquals(expectedNew, diff.getNew());

        Set<String> expectedStale = new HashSet<>();
        for (int i = -5; i < 0; i++) {
            expectedStale.add("ID" + i);
        }
        assertCollectionEquals(expectedStale, diff.getStaleIds());
    }

    @Test
    public void apply() {
        Collection<Integer> newData = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Collection<Integer> oldData = new HashSet<>(Arrays.asList(2, 63, 3, 4, 5, 65, 100, 5000, 0));

        Synchronisation<Integer, Integer> synchronisation = new Synchronisation<>(oldData, newData, Function.identity());
        Synchronisation.Diff<Integer, Integer> diff = synchronisation.diff();

        @SuppressWarnings("unchecked")
        FullRepository<Integer, Integer> mock = (FullRepository<Integer, Integer>) mock(FullRepository.class);

        diff.apply(mock);

        verify(mock).deleteById(anyCollection());
        verify(mock).insert(anyCollection());
        verify(mock).update(anyCollection());
    }
}