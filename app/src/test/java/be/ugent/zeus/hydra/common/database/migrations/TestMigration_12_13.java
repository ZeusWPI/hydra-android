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

package be.ugent.zeus.hydra.common.database.migrations;

import android.app.Instrumentation;
import androidx.room.testing.LocalMigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;

import java.io.IOException;

import be.ugent.zeus.hydra.common.database.Database;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class TestMigration_12_13 {

    @Rule
    public final LocalMigrationTestHelper testHelper;

    {
        Instrumentation mockInstrumentation = mock(Instrumentation.class);
        when(mockInstrumentation.getTargetContext()).thenReturn(ApplicationProvider.getApplicationContext());
        when(mockInstrumentation.getContext()).thenReturn(ApplicationProvider.getApplicationContext());
        testHelper = new LocalMigrationTestHelper(mockInstrumentation, Database.class.getCanonicalName());
    }

    @Test
    public void testMigration() throws IOException {
        // There is no data to test, since we just add a new column.
        SupportSQLiteDatabase version12 = testHelper.createDatabase("test-db", 12);
        version12.close();
        testHelper.runMigrationsAndValidate("test-db", 13, true, new Migration_12_13());
    }

}
