package be.ugent.zeus.hydra.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * A loader that will listen for changes from a {@link android.support.v4.content.LocalBroadcastManager}.
 *
 * @author Niko Strijbol
 */
public abstract class BroadcastLoader<D> extends AbstractLoader<D> {

    private final IntentFilter filter;
    private final BroadcastReceiver receiver = getReceiver();

    public BroadcastLoader(Context context, IntentFilter filter) {
        super(context);
        this.filter = filter;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        // Register the broadcast receiver.
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.registerReceiver(receiver, filter);
    }

    @Override
    protected void onReset() {
        super.onReset();
        // Unregister the content monitor.
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.unregisterReceiver(receiver);
    }

    protected BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onContentChanged();
            }
        };
    }
}