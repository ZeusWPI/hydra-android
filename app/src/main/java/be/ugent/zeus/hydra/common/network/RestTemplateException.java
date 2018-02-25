package be.ugent.zeus.hydra.common.network;

import be.ugent.zeus.hydra.common.request.RequestException;

/**
 * Exception thrown when the rest template could not be made.
 *
 * @author Niko Strijbol
 */
public class RestTemplateException extends RequestException {

    public RestTemplateException(Throwable cause) {
        super(cause);
    }
}
