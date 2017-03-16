package be.ugent.zeus.hydra.data.network.requests.resto;

import android.content.Context;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.data.models.resto.RestoOverview;
import be.ugent.zeus.hydra.data.network.ProcessableCacheRequest;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;

/**
 * Filtered resto request.
 *
 * @author Niko Strijbol
 */
public class FilteredMenuRequest extends ProcessableCacheRequest<RestoOverview, RestoOverview> {

    public FilteredMenuRequest(Context context, boolean shouldRefresh) {
        super(context, new RestoMenuRequest(context), shouldRefresh);
    }

    @NonNull
    @Override
    protected RestoOverview transform(@NonNull RestoOverview data) throws RequestFailureException {
        RestoOverview.filter(data, context);
        return data;
    }
}