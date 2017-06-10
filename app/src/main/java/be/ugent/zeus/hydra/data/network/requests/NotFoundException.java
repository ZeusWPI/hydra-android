package be.ugent.zeus.hydra.data.network.requests;

import be.ugent.zeus.hydra.data.network.exceptions.RequestException;

/**
 * @author Niko Strijbol
 */
public class NotFoundException extends RequestException {

    public NotFoundException(String message) {
        super(message);
    }

}
