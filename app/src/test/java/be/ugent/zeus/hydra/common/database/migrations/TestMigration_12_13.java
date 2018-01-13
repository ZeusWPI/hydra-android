package be.ugent.zeus.hydra.common.database.migrations;

import android.app.Instrumentation;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.testing.LocalMigrationTestHelper;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.common.database.migrations.Migration_12_13;
import be.ugent.zeus.hydra.data.database.Database;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = 26)
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
public class TestMigration_12_13 {

    @Rule
    public LocalMigrationTestHelper testHelper;

    {
        Instrumentation mockInstrumentation = mock(Instrumentation.class);
        when(mockInstrumentation.getTargetContext()).thenReturn(RuntimeEnvironment.application);
        when(mockInstrumentation.getContext()).thenReturn(RuntimeEnvironment.application);
        testHelper = new LocalMigrationTestHelper(mockInstrumentation, Database.class.getCanonicalName());
    }

    @Test
    @Ignore
    public void testMigration() throws IOException {
        // There is no data to test, since we just add a new column.
        SupportSQLiteDatabase version12 = testHelper.createDatabase("test-db", 12);
        version12.close();
        testHelper.runMigrationsAndValidate("test-db", 13, true, new Migration_12_13());
    }

}