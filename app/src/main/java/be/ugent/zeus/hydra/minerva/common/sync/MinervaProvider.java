package be.ugent.zeus.hydra.minerva.common.sync;

import androidx.sqlite.db.SupportSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQueryBuilder;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.minerva.provider.CourseContract;

/**
 * We expose a read-only list of the courses.
 *
 * You must hold the {@link be.ugent.zeus.hydra.Manifest.permission#READ_COURSES} permission to access them.
 *
 * @author Niko Strijbol
 */
public class MinervaProvider extends ContentProvider {

    private static final String TAG = "MinervaProvider";

    private static final String MIME_TYPE = "/vnd." + CourseContract.Provider.AUTHORITY + "." + CourseContract.TABLE_NAME;

    private static final int ALL_COURSES = 1;
    private static final int ONE_COURSE = 2;

    private Database database;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CourseContract.Provider.AUTHORITY, CourseContract.TABLE_NAME, ALL_COURSES);
        uriMatcher.addURI(CourseContract.Provider.AUTHORITY, CourseContract.TABLE_NAME + "/*", ONE_COURSE);
    }

    @Override
    public boolean onCreate() {
        this.database = getDatabase();
        return true;
    }

    @VisibleForTesting
    protected Database getDatabase() {
        return Database.get(getContext());
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.d(TAG, "query: receiving " + uri);

        String whereClause = null;

        switch (uriMatcher.match(uri)) {
            case ALL_COURSES: break;
            case ONE_COURSE:
                whereClause = BaseColumns._ID + " = " + uri.getLastPathSegment();
                break;
            default:
                return null;
        }

        StringBuilder where = new StringBuilder();
        if (!TextUtils.isEmpty(whereClause)) {
            where.append('(');
            where.append(whereClause);
            where.append(')');
        }

        // Tackle user selection (taken from the SQLiteQueryBuilder).
        if (selection != null && !selection.isEmpty()) {
            if (!TextUtils.isEmpty(whereClause)) {
                where.append(" AND ");
            }

            where.append('(');
            where.append(selection);
            where.append(')');
        }

        SupportSQLiteQuery query = SupportSQLiteQueryBuilder.builder(CourseContract.TABLE_NAME)
                .columns(projection)
                .orderBy(sortOrder)
                .selection(where.toString(), selectionArgs)
                .create();

        return database.query(query);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ALL_COURSES:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + MIME_TYPE;
            case ONE_COURSE:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + MIME_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Don't allow inserts.
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Don't allow deletions.
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Don't allow updates.
        return 0;
    }
}