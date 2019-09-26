package be.ugent.zeus.hydra.common.network;

import be.ugent.zeus.hydra.common.request.RequestException;

/**
 * @author Niko Strijbol
 */
class InvalidFormatException extends RequestException {

    InvalidFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}
