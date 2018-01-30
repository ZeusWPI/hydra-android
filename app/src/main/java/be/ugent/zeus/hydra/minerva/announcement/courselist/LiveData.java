package be.ugent.zeus.hydra.minerva.announcement.courselist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import be.ugent.zeus.hydra.common.database.RepositoryFactory;
import be.ugent.zeus.hydra.minerva.sync.SyncBroadcast;
import be.ugent.zeus.hydra.minerva.announcement.Announcement;
import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementRepository;
import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Result;

import java.util.List;

/**
 * @author Niko Strijbol
 */
class LiveData extends BaseLiveData<Result<List<Announcement>>> {

    private final Context applicationContext;
    private final AnnouncementRepository dao;
    private final Course course;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SyncBroadcast.SYNC_PROGRESS_WHATS_NEW.equals(action)
                    && course.getId().equals(intent.getStringExtra(SyncBroadcast.ARG_SYNC_PROGRESS_COURSE))) {
                loadData(Bundle.EMPTY);
            }
        }
    };

    LiveData(Context context, Course course) {
        this.applicationContext = context.getApplicationContext();
        this.dao = RepositoryFactory.getAnnouncementRepository(applicationContext);
        this.course = course;
        loadData(Bundle.EMPTY);
    }

    @Override
    protected void loadData(@Nullable Bundle args) {
        new AsyncTask<Void, Void, Result<List<Announcement>>>() {

            @Override
            protected Result<List<Announcement>> doInBackground(Void... voids) {
                return Result.Builder.fromData(dao.getMostRecentFirst(course.getId()));
            }

            @Override
            protected void onPostExecute(Result<List<Announcement>> agendaItemResult) {
                setValue(agendaItemResult);
            }
        }.execute();
    }

    @Override
    protected void onActive() {
        super.onActive();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(applicationContext);
        manager.registerReceiver(receiver, getFilter());
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(applicationContext);
        manager.unregisterReceiver(receiver);
    }

    private static IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SyncBroadcast.SYNC_PROGRESS_WHATS_NEW);
        return filter;
    }
}