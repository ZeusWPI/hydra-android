package be.ugent.zeus.hydra.data.sync.minerva;

import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQueryBuilder;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import be.ugent.zeus.hydra.data.database.minerva2.MinervaDatabase;
import be.ugent.zeus.hydra.data.database.minerva2.course.CourseTable;

/**
 * We only expose a read-only version of the list of courses for now.
 *
 * @author Niko Strijbol
 */
public class MinervaProvider extends ContentProvider {

    private static final String TAG = "MinervaProvider";

    private static final int ALL_COURSES = 1;
    private static final int ONE_COURSE = 2;

    private MinervaDatabase database;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CourseTable.Provider.AUTHORITY, CourseTable.TABLE_NAME, ALL_COURSES);
        uriMatcher.addURI(CourseTable.Provider.AUTHORITY, CourseTable.TABLE_NAME + "/*", ONE_COURSE);
    }

    @Override
    public boolean onCreate() {
        // TODO: is this too expensive?
        this.database = getDatabase();
        return true;
    }

    @VisibleForTesting
    protected MinervaDatabase getDatabase() {
        return MinervaDatabase.get(getContext());
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.d(TAG, "query: receiving " + uri);

        String whereClause = null;

        switch (uriMatcher.match(uri)) {
            case ALL_COURSES: break;
            case ONE_COURSE:
                whereClause = BaseColumns._ID + " = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Uri was not recognized.");
        }

        StringBuilder where = new StringBuilder();
        if (!TextUtils.isEmpty(whereClause)) {
            where.append('(');
            where.append(whereClause);
            where.append(')');
        }
        // Tackle user selection (taken from the SQLiteQueryBuilder.
        if (selection != null && selection.length() > 0) {
            if (!TextUtils.isEmpty(whereClause)) {
                where.append(" AND ");
            }

            where.append('(');
            where.append(selection);
            where.append(')');
        }

        SupportSQLiteQuery query = SupportSQLiteQueryBuilder.builder(CourseTable.TABLE_NAME)
                .columns(projection)
                .orderBy(sortOrder)
                .selection(where.toString(), selectionArgs)
                .create();

        return database.query(query);
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ALL_COURSES:
                return "vnd.android.cursor.dir/vnd." + CourseTable.Provider.AUTHORITY + "." + CourseTable.TABLE_NAME;
            case ONE_COURSE:
                return "vnd.android.cursor.item/vnd." + CourseTable.Provider.AUTHORITY + "." + CourseTable.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Uri was not recognized.");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Don't allow inserts.
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Don't allow deletions.
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Don't allow updates.
        return 0;
    }
}