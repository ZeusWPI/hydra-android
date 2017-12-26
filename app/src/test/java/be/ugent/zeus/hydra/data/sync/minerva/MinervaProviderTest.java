package be.ugent.zeus.hydra.data.sync.minerva;

import android.content.ContentResolver;
import android.database.Cursor;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.database.minerva2.AbstractDaoTest;
import be.ugent.zeus.hydra.data.database.minerva2.MinervaDatabase;
import be.ugent.zeus.hydra.data.dto.minerva.CourseDTO;
import be.ugent.zeus.hydra.minerva.provider.contract.CourseContract;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
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
@Config(constants = BuildConfig.class)
@RequiresApi(api = 26)
public class MinervaProviderTest extends AbstractDaoTest {

    @Before
    public void setUp() throws IOException {
        super.setUp();
        MinervaProvider provider = new MinervaProvider() {
            @Override
            protected MinervaDatabase getDatabase() {
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