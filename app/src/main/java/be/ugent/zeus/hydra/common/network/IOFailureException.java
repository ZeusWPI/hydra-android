package be.ugent.zeus.hydra.common.network;

import be.ugent.zeus.hydra.common.request.RequestException;

/**
 * This exception should be thrown when the request could not be completed due to IO failure. The most common use will
 * be network failure.
 *
 * @author Niko Strijbol
 */
public class IOFailureException extends RequestException {

    public IOFailureException(Throwable cause) {
        super(cause);
    }
}
