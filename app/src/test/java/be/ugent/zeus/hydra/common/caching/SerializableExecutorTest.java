package be.ugent.zeus.hydra.common.caching;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
public class SerializableExecutorTest {

    private static final String TEST_FILE_1 = "test_object";
    private static final String TEST_FILE_2 = "test_object_2";

    //The temporary directory where we save the files
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    //The executor to use during tests.
    private SerializableExecutor executor;
    //Where the files are saved.
    private File testFolder;

    @Before
    public void setUp() throws IOException {
        this.testFolder = folder.newFolder();
        this.executor = new SerializableExecutor(testFolder);
    }

    /**
     * Test both the saving and reading in the same method.
     */
    @Test
    public void test() throws Exception {

        //Save the data
        CacheObject<TestObject> testObject = new CacheObject<>(new TestObject());
        executor.save(TEST_FILE_1, testObject);

        CacheObject<TestObject> testObject2 = new CacheObject<>(new TestObject("Another object"));
        executor.save(TEST_FILE_2, testObject2);

        //Test the data exists.
        File saved = new File(testFolder, TEST_FILE_1);
        File saved2 = new File(testFolder, TEST_FILE_2);
        assertTrue(saved.exists());
        assertTrue(saved2.exists());

        TestObject readTestObject = executor.<TestObject>read(TEST_FILE_1).getData();
        TestObject readTestObject2 = executor.<TestObject>read(TEST_FILE_2).getData();

        assertEquals(testObject.getData(), readTestObject);
        assertEquals(testObject2.getData(), readTestObject2);

        assertNotEquals(readTestObject, readTestObject2);
    }

    @Test(expected = CacheException.class)
    public void testNonExisting() throws CacheException {
        executor.read("does_not_exist");
    }
}