package be.ugent.zeus.hydra.ui.minerva;

import android.arch.lifecycle.LiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import be.ugent.zeus.hydra.data.database.minerva.AgendaDao;
import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.data.network.requests.NotFoundException;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.data.network.requests.Result;

/**
 * @author Niko Strijbol
 */
public class AgendaLiveData extends LiveData<Result<AgendaItem>> {

    private final Context applicationContext;
    private final AgendaDao dao;
    private final int id;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData(Bundle.EMPTY);
        }
    };

    public AgendaLiveData(Context context, int id) {
        this.applicationContext = context;
        this.dao = new AgendaDao(context);
        this.id = id;
        loadData(Bundle.EMPTY);
    }

    private void loadData(Bundle args) {
        new AsyncTask<Void, Void, Result<AgendaItem>>() {

            @Override
            protected Result<AgendaItem> doInBackground(Void... voids) {

                AgendaItem item = dao.getItem(id);

                if (item == null) {
                    return Result.Builder.fromException(new NotFoundException("Agenda item with ID " + id + " was not found."));
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