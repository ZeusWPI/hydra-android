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

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * @author Niko Strijbol
 */
public class Migration_13_14 extends Migration {

    private static final String TAG = "Migration_13_14";

    public Migration_13_14() {
        super(13, 14);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        Log.i(TAG, "Migrating database from " + this.startVersion + " to " + this.endVersion);

        database.execSQL("ALTER TABLE minerva_courses ADD COLUMN ignore_announcements INTEGER NOT NULL DEFAULT 0");
        database.execSQL("ALTER TABLE minerva_courses ADD COLUMN ignore_calendar INTEGER NOT NULL DEFAULT 0");

        Log.i(TAG, "Migration completed.");
    }
}