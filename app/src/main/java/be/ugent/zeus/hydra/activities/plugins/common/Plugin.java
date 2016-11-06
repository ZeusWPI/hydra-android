package be.ugent.zeus.hydra.activities.plugins.common;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * @author Niko Strijbol
 */
public abstract class Plugin {

    private ContextProvider.Provider provider;

    final void setProvider(ContextProvider.Provider provider) {
        this.provider = provider;
    }

    @NonNull
    protected ContextProvider.Provider getHost() {
        if(provider == null) {
            throw new IllegalStateException("getHost was called too early.");
        }
        return provider;
    }

    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {

    }

    /**
     * In an activity, this is called after {@link android.app.Activity#setContentView(int)}.
     */
    protected void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    @CallSuper
    protected void onStart() {

    }

    @CallSuper
    protected void onResume() {

    }

    @CallSuper
    protected void onPause() {

    }

    @CallSuper
    protected void onStop() {

    }

    @CallSuper
    protected void onRestart() {

    }

    @CallSuper
    protected void onDestroy() {

    }
}