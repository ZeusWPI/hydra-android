package be.ugent.zeus.hydra.loaders.changes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;
import be.ugent.zeus.hydra.loaders.BroadcastLoader;
import java8.util.function.BiPredicate;

/**
 * General broadcast receiver for use with {@link BroadcastLoader}. The receiver supports conditions with the
 * {@link #BroadcastListener(Loader, BiPredicate)} constructor. If the predicate is not given, receiving an intent
 * will always result in a change call.
 *
 * @author Niko Strijbol
 */
public class BroadcastListener extends BroadcastReceiver {

    private static final String TAG = "ContentChangedReceiver";

    private final Loader<?> loader;
    private final BiPredicate<Context, Intent> predicate;

    public BroadcastListener(Loader<?> loader) {
        this(loader, null);
    }

    public BroadcastListener(Loader<?> loader, BiPredicate<Context, Intent> predicate) {
        this.loader = loader;
        this.predicate = predicate;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (predicate == null || predicate.test(context, intent)) {
            loader.onContentChanged();
        }
    }
}