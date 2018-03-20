package be.ugent.zeus.hydra.minerva.common;

import be.ugent.zeus.hydra.common.request.RequestException;

/**
 * @author Niko Strijbol
 */
public class AuthException extends RequestException {
    AuthException(Throwable cause) {
        super(cause);
    }
}