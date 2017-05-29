package be.ugent.zeus.hydra.ui.common.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * A loader that will listen for changes from a {@link android.support.v4.content.LocalBroadcastManager}.
 *
 * @author Niko Strijbol
 */
@Deprecated
public abstract class BroadcastLoader<D> extends AbstractLoader<D> {

    private final IntentFilter filter;
    private BroadcastReceiver receiver;

    public BroadcastLoader(Context context, IntentFilter filter) {
        super(context);
        this.filter = filter;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        receiver = getReceiver();
        // Register the broadcast receiver.
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.registerReceiver(receiver, filter);
    }

    @Override
    protected void onReset() {
        super.onReset();
        if (receiver != null) {
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
            manager.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    protected BroadcastReceiver getReceiver() {
        return new BroadcastListener(this);
    }
}