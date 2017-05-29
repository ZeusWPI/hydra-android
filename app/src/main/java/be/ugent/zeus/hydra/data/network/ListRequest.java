package be.ugent.zeus.hydra.data.network;

import android.os.Bundle;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.data.network.exceptions.PartialDataException;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import java8.util.function.Function;

import java.util.Arrays;
import java.util.List;

/**
 * A request that converts the array result of another request to a list request.
 *
 * @author Niko Strijbol
 *
 * @deprecated Use {@link be.ugent.zeus.hydra.data.network.requests.Requests#map(Request, Function)} instead.
 */
@Deprecated
public class ListRequest<R> implements Request<List<R>> {

    private final Request<R[]> wrapping;

    public ListRequest(Request<R[]> wrapping) {
        this.wrapping = wrapping;
    }

    @NonNull
    @Override
    public List<R> performRequest(Bundle args) throws RequestFailureException, PartialDataException {
        return Arrays.asList(wrapping.performRequest(args));
    }
}