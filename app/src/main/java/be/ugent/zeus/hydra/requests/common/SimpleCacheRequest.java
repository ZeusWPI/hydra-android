package be.ugent.zeus.hydra.requests.common;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.caching.CacheRequest;

import java.io.Serializable;

/**
 * A simple request that returns the data as it gets it.
 *
 * @author Niko Strijbol
 */
public class SimpleCacheRequest<D extends Serializable> extends ProcessableCacheRequest<D, D> {

    public SimpleCacheRequest(Context context, CacheRequest<D> request, boolean shouldRefresh) {
        super(context, request, shouldRefresh);
    }

    @NonNull
    @Override
    protected D transform(@NonNull D data) {
        return data;
    }
}