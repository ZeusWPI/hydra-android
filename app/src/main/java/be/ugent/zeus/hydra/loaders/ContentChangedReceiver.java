package be.ugent.zeus.hydra.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;
import java8.util.function.BiPredicate;

/**
 * General broadcast receiver for use with {@link BroadcastLoader}. The receiver supports conditions with the
 * {@link #ContentChangedReceiver(Loader, BiPredicate)} constructor. If the predicate is not given, receiving an intent
 * will always result in a change call.
 *
 * @author Niko Strijbol
 */
public class ContentChangedReceiver extends BroadcastReceiver {

    private final Loader<?> loader;

    private final BiPredicate<Context, Intent> predicate;

    public ContentChangedReceiver(Loader<?> loader) {
        this(loader, null);
    }

    public ContentChangedReceiver(Loader<?> loader, BiPredicate<Context, Intent> predicate) {
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