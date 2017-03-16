package be.ugent.zeus.hydra.data.network;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;

import java.util.Arrays;
import java.util.List;

/**
 * A request that converts the array result of another request to a list request.
 *
 * @author Niko Strijbol
 */
public class FromArrayRequest<R> implements Request<List<R>> {

    private final Request<R[]> wrapping;

    public FromArrayRequest(Request<R[]> wrapping) {
        this.wrapping = wrapping;
    }

    @NonNull
    @Override
    public List<R> performRequest() throws RequestFailureException {
        return Arrays.asList(wrapping.performRequest());
    }
}