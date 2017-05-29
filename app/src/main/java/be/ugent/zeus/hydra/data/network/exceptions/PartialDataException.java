package be.ugent.zeus.hydra.data.network.exceptions;

import java.io.Serializable;

/**
 * Thrown when a request encountered an error, but did gather partial or outdated data. Note that the data in this
 * exception is only useful if partial data makes sense, e.g. a cached request when the network is down might return
 * the cached data anyway.
 *
 * @author Niko Strijbol
 */
public class PartialDataException extends RequestException {

    private final Serializable data;

    public PartialDataException(Throwable cause, Serializable data) {
        super(cause);
        this.data = data;
    }

    public <R> R getData() {
        return (R) data;
    }
}