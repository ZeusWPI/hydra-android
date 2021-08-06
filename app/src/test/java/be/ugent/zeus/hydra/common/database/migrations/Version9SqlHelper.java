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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Constructs the database as it was at scheme version 9.
 *
 * @author Niko Strijbol
 */
public class Version9SqlHelper extends SQLiteOpenHelper {

    Version9SqlHelper(Context context, String name) {
        super(context, name, null, 9);
    }

    /**
     * Create all tables.
     */
    static void createTables(SQLiteDatabase db) {
        // Create the course table.
        db.execSQL("CREATE TABLE IF NOT EXISTS minerva_courses (" +
                "_id TEXT PRIMARY KEY," +
                "code TEXT," +
                "title TEXT," +
                "description TEXT," +
                "tutor TEXT," +
                "student TEXT," +
                "academic_year INTEGER," +
                "ordering INTEGER NOT NULL DEFAULT 0" +
                ")");

        // Create the calendar table.
        db.execSQL("CREATE TABLE IF NOT EXISTS minerva_agenda (" +
                "_id INTEGER PRIMARY KEY," +
                "course TEXT NOT NULL REFERENCES minerva_courses(_id) ON DELETE CASCADE," +
                "title TEXT," +
                "content TEXT," +
                "start_date INTEGER," +
                "end_date INTEGER," +
                "location TEXT," +
                "type TEXT," +
                "last_edit_user TEXT," +
                "last_edit INTEGER," +
                "last_edit_type TEXT," +
                "calendar_id INTEGER," +
                "IS_MERGED INTEGER NOT NULL DEFAULT 0" +
                ")");

        // Create the announcements table.
        db.execSQL("CREATE TABLE IF NOT EXISTS minerva_announcements (" +
                "_id INTEGER PRIMARY KEY," +
                "course TEXT NOT NULL REFERENCES minerva_courses(_id) ON DELETE CASCADE," +
                "title TEXT," +
                "content TEXT," +
                "email_sent INTEGER," +
                "sticky_until INTEGER," +
                "last_edit_user TEXT," +
                "date INTEGER," +
                "read_at INTEGER" +
                ")");
    }

    static void deleteTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS minerva_announcements");
        db.execSQL("DROP TABLE IF EXISTS minerva_agenda");
        db.execSQL("DROP TABLE IF EXISTS minerva_courses");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Here we create the database as it was in version 9.
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required in version 9.
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required in version 9.
    }
}
