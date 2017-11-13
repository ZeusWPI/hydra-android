package be.ugent.zeus.hydra.ui.minerva.overview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import be.ugent.zeus.hydra.data.database.minerva2.RepositoryFactory;
import be.ugent.zeus.hydra.data.sync.minerva.SyncBroadcast;
import be.ugent.zeus.hydra.domain.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.domain.models.minerva.Course;
import be.ugent.zeus.hydra.domain.repository.AgendaItemRepository;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;
import be.ugent.zeus.hydra.repository.requests.Result;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class AgendaLiveData extends BaseLiveData<Result<List<AgendaItem>>> {

    private final Context applicationContext;
    private final AgendaItemRepository dao;
    private final Course course;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData(Bundle.EMPTY);
        }
    };

    public AgendaLiveData(Context context, Course course) {
        this.applicationContext = context.getApplicationContext();
        this.dao = RepositoryFactory.getAgendaItemRepository(applicationContext);
        this.course = course;
        loadData(Bundle.EMPTY);
    }

    protected void loadData(Bundle args) {
        new AsyncTask<Void, Void, Result<List<AgendaItem>>>() {

            @Override
            protected Result<List<AgendaItem>> doInBackground(Void... voids) {
                return Result.Builder.fromData(dao.getAllForCourseFuture(course.getId()));
            }

            @Override
            protected void onPostExecute(Result<List<AgendaItem>> agendaItemResult) {
                setValue(agendaItemResult);
            }
        }.execute();
    }

    @Override
    protected void onActive() {
        super.onActive();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(applicationContext);
        manager.registerReceiver(receiver, new IntentFilter(SyncBroadcast.SYNC_AGENDA));
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(applicationContext);
        manager.unregisterReceiver(receiver);
    }
}