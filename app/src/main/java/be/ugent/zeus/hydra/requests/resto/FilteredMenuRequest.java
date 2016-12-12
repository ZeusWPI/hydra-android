package be.ugent.zeus.hydra.requests.resto;

import android.content.Context;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;

/**
 * Filtered resto request.
 *
 * @author Niko Strijbol
 */
public class FilteredMenuRequest extends ProcessableCacheRequest<RestoOverview, RestoOverview> {

    public FilteredMenuRequest(Context context, boolean shouldRefresh) {
        super(context, new RestoMenuRequest(), shouldRefresh);
    }

    @NonNull
    @Override
    protected RestoOverview transform(@NonNull RestoOverview data) throws RequestFailureException {
        RestoOverview.filter(data, context);
        return data;
    }
}