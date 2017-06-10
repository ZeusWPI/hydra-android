package be.ugent.zeus.hydra.ui.main.minerva;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import be.ugent.zeus.hydra.data.database.minerva.CourseDao;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.data.network.requests.Result;
import be.ugent.zeus.hydra.repository.data.RefreshingLiveData;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class CourseLiveData extends RefreshingLiveData<List<Course>> {

    private final IntentFilter intentFilter = new IntentFilter(SyncBroadcast.SYNC_COURSES);
    public final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData(Bundle.EMPTY);
        }
    };

    public CourseLiveData(Context context) {
        super(context, new CourseRequest(context));
    }

    @Override
    protected void onActive() {
        super.onActive();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.unregisterReceiver(receiver);
    }

    /**
     * Get all courses, given the order.
     */
    public static class CourseRequest implements Request<List<Course>> {

        private final CourseDao courseDao;

        public CourseRequest(Context context) {
            this.courseDao = new CourseDao(context);
        }

        @NonNull
        @Override
        public Result<List<Course>> performRequest(@Nullable Bundle args) {
            return Result.Builder.fromData(courseDao.getAll());
        }
    }
}