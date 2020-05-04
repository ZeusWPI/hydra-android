package be.ugent.zeus.hydra.common.database.migrations;

import android.app.Instrumentation;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.testing.LocalMigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;

import java.io.IOException;

import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.common.database.Database;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = 26)
@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class TestMigration_14_15 {

    @Rule
    public final LocalMigrationTestHelper testHelper;

    {
        Instrumentation mockInstrumentation = mock(Instrumentation.class);
        when(mockInstrumentation.getTargetContext()).thenReturn(ApplicationProvider.getApplicationContext());
        when(mockInstrumentation.getContext()).thenReturn(ApplicationProvider.getApplicationContext());
        testHelper = new LocalMigrationTestHelper(mockInstrumentation, Database.class.getCanonicalName());
    }

    @Test
    @Config(sdk = Build.VERSION_CODES.KITKAT) // TODO: Sqlite version is too old in robolectric!
    public void testMigration() throws IOException {
        // There is no data to test, since we just add a new column.
        SupportSQLiteDatabase version14 = testHelper.createDatabase("test-db", 14);
        version14.close();
        testHelper.runMigrationsAndValidate("test-db", 15, true, new Migration_14_15());
    }

}
