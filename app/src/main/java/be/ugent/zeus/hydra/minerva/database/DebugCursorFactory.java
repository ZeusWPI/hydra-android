package be.ugent.zeus.hydra.minerva.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

/**
 * Can be used to log SQL statements.
 *
 * @author Niko Strijbol
 */
public class DebugCursorFactory implements SQLiteDatabase.CursorFactory {

    private boolean debugQueries = false;

    public DebugCursorFactory() {
        this.debugQueries = false;
    }

    public DebugCursorFactory(boolean debugQueries) {
        this.debugQueries = debugQueries;
    }

    @Override
    public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
        if (debugQueries) {
            Log.d("SQL", query.toString());
        }
        return new SQLiteCursor(masterQuery, editTable, query);
    }
}