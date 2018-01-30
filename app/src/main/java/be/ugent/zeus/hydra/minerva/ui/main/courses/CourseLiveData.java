package be.ugent.zeus.hydra.minerva.ui.main.courses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;

import be.ugent.zeus.hydra.common.database.RepositoryFactory;
import be.ugent.zeus.hydra.minerva.sync.SyncBroadcast;
import be.ugent.zeus.hydra.minerva.Course;
import be.ugent.zeus.hydra.minerva.CourseRepository;
import be.ugent.zeus.hydra.common.arch.data.RequestLiveData;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;

import java.util.List;

/**
 * @author Niko Strijbol
 */
class CourseLiveData extends RequestLiveData<List<Pair<Course, Long>>> {

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData(Bundle.EMPTY);
        }
    };

    CourseLiveData(Context context) {
        super(context, new CourseRequest(context));
    }

    @Override
    protected void onActive() {
        super.onActive();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.registerReceiver(receiver, getFilter());
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
    public static class CourseRequest implements Request<List<Pair<Course, Long>>> {

        private final CourseRepository courseDao;

        CourseRequest(Context context) {
            this.courseDao = RepositoryFactory.getCourseRepository(context);
        }

        @NonNull
        @Override
        public Result<List<Pair<Course, Long>>> performRequest(@Nullable Bundle args) {
            return Result.Builder.fromData(courseDao.getAllAndUnreadInOrder());
        }
    }

    private static IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SyncBroadcast.SYNC_PROGRESS_WHATS_NEW);
        filter.addAction(SyncBroadcast.SYNC_COURSES);
        return filter;
    }
}