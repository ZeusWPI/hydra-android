package be.ugent.zeus.hydra.ui.minerva;

import be.ugent.zeus.hydra.common.request.RequestException;

/**
 * @author Niko Strijbol
 */
public class NotFoundException extends RequestException {

    public NotFoundException(String message) {
        super(message);
    }

}
