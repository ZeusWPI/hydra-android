package be.ugent.zeus.hydra.common.network;

import be.ugent.zeus.hydra.common.request.RequestException;

/**
 * @author Niko Strijbol
 */
public class InvalidFormatException extends RequestException {

    public InvalidFormatException(String message) {
        super(message);
    }

}