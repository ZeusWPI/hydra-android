package be.ugent.zeus.hydra.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import be.ugent.zeus.hydra.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.minerva.database.CourseDao;
import be.ugent.zeus.hydra.models.minerva.Courses;
import be.ugent.zeus.hydra.requests.minerva.CoursesMinervaRequest;

/**
 * @author Niko Strijbol
 */
public class MinervaSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "MinervaSyncAdapter";

    public MinervaSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public MinervaSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(TAG, "Starting sync...");

        CoursesMinervaRequest request = new CoursesMinervaRequest(getContext(), account, null);

        try {
            Log.d(TAG, "Performing request");
            Courses courses = request.performRequest();

            CourseDao.synchronise(getContext(), courses.getCourses());
            Log.d(TAG, "Sync done.");

        } catch (RequestFailureException e) {
            Log.w(TAG, "Sync error.", e);
        }

    }
}
