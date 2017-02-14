package be.ugent.zeus.hydra.minerva.sync;

import java8.util.function.Functions;
import org.junit.Test;

import java.util.*;

import static be.ugent.zeus.hydra.testing.Assert.assertCollectionEquals;

/**
 * @author Niko Strijbol
 */
public class SynchronisationTest {

    @Test
    public void classify() throws Exception {

        Set<Integer> newData = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Set<Integer> oldData = new HashSet<>(Arrays.asList(2, 63, 3, 4, 5, 65, 100, -56565, 0));

        Synchronisation<Integer, Integer> synchronisation = new Synchronisation<>(oldData, newData, Functions.identity());
        Synchronisation.Diff<Integer, Integer> diff = synchronisation.diff();

        Collection<Integer> expectedNew = Collections.singleton(1);
        Collection<Integer> expectedStale = Arrays.asList(63, 65, 100, -56565, 0);
        Collection<Integer> expectedUpdated = Arrays.asList(2, 3, 4, 5);

        assertCollectionEquals(expectedNew, diff.getNew());
        assertCollectionEquals(expectedStale, diff.getStaleIds());
        assertCollectionEquals(expectedUpdated, diff.getUpdated());
    }

    @Test
    public void classifyEmpty() throws Exception {

        Set<Integer> newData = Collections.emptySet();
        Set<Integer> oldData = new HashSet<>(Arrays.asList(2, 63, 3, 4, 5, 65, 100, -56565, 0));

        Synchronisation<Integer, Integer> synchronisation = new Synchronisation<>(oldData, newData, Functions.identity());
        Synchronisation.Diff<Integer, Integer> diff = synchronisation.diff();

        assertCollectionEquals(Collections.emptyList(), diff.getNew());
        assertCollectionEquals(oldData, diff.getStaleIds());
        assertCollectionEquals(Collections.emptyList(), diff.getUpdated());
    }

    private static class TestObject {
        final String id;
        final String data = "Nice object!";

        private TestObject(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;
            return java8.util.Objects.equals(id, that.id);
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
}