package be.ugent.zeus.hydra.minerva.common.sync;

import android.content.ContentResolver;
import android.database.Cursor;

import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.minerva.AbstractDaoTest;
import be.ugent.zeus.hydra.minerva.course.database.CourseDTO;
import be.ugent.zeus.hydra.minerva.provider.CourseContract;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowContentResolver;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class MinervaProviderTest extends AbstractDaoTest {

    @Before
    public void setUp() throws IOException {
        super.setUp();
        MinervaProvider provider = new MinervaProvider() {
            @Override
            protected Database getDatabase() {
                return database;
            }
        };
        provider.onCreate();
        ShadowContentResolver.registerProviderInternal(CourseContract.Provider.AUTHORITY, provider);
    }

    @Test
    public void testAll() {
        ContentResolver contentResolver = RuntimeEnvironment.application.getContentResolver();

        // TODO: test more stuff.
        Cursor c = contentResolver.query(
                CourseContract.Provider.CONTENT_URI,
                new String[]{CourseContract.Columns.ID},
                null,
                null,
                null);

        assertNotNull(c);
        assertEquals(courses.size(), c.getCount());
        assertEquals(1, c.getColumnCount());

        Set<String> ids = this.courses.stream().map(CourseDTO::getId).collect(Collectors.toSet());

        while(c.moveToNext()) {
            assertTrue(ids.contains(c.getString(c.getColumnIndexOrThrow(CourseContract.Columns.ID))));
        }
    }
}