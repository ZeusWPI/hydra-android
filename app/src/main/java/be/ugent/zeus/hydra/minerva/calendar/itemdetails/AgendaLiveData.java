package be.ugent.zeus.hydra.minerva.calendar.itemdetails;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import be.ugent.zeus.hydra.common.database.RepositoryFactory;
import be.ugent.zeus.hydra.minerva.common.sync.SyncBroadcast;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItem;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItemRepository;
import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * @author Niko Strijbol
 */
class AgendaLiveData extends BaseLiveData<Result<AgendaItem>> {

    private final Context applicationContext;
    private final AgendaItemRepository dao;
    private final int id;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData();
        }
    };

    AgendaLiveData(Context context, int id) {
        this.applicationContext = context;
        this.dao = RepositoryFactory.getAgendaItemRepository(context);
        this.id = id;
        loadData();
    }

    @Override
    @SuppressLint("StaticFieldLeak")
    protected void loadData(@NonNull Bundle args) {
        new AsyncTask<Void, Void, Result<AgendaItem>>() {
            @Override
            protected Result<AgendaItem> doInBackground(Void... voids) {
                AgendaItem item = dao.getOne(id);
                if (item == null) {
                    return Result.Builder.fromException(new CalendarItemNotFoundException("Agenda item with ID " + id + " was not found."));
                } else {
                    return Result.Builder.fromData(item);
                }
            }

            @Override
            protected void onPostExecute(Result<AgendaItem> agendaItemResult) {
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