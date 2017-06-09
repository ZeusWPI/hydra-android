package be.ugent.zeus.hydra.repository.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.Result;

/**
 * @author Niko Strijbol
 */
public class RefreshLiveData extends LiveData<Boolean> {

    private final Context applicationContext;
    private final BroadcastReceiver receiver;

    private RefreshLiveData(Context context) {
        this.applicationContext = context.getApplicationContext();
        this.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setValue(true);
            }
        };
    }

    public static <D> LiveData<Boolean> build(Context context, LiveData<Result<D>> source) {
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();
        RefreshLiveData refreshLiveData = new RefreshLiveData(context);
        result.addSource(source, data -> result.setValue(data != null && !data.isDone()));
        result.addSource(refreshLiveData, result::setValue);
        return result;
    }

    @Override
    protected void onActive() {
        super.onActive();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(applicationContext);
        manager.registerReceiver(receiver, RefreshBroadcast.getBroadcastFilter());
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(applicationContext);
        manager.unregisterReceiver(receiver);
    }
}