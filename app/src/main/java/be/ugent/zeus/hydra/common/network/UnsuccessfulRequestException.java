package be.ugent.zeus.hydra.common.network;

import java.io.IOException;

/**
 * @author Niko Strijbol
 */
public class UnsuccessfulRequestException extends IOException {

    private final int httpCode;

    UnsuccessfulRequestException(int httpCode) {
        super("Unsuccessful HTTP request, response code is " + httpCode);
        this.httpCode = httpCode;
    }

    @SuppressWarnings("unused")
    public int getHttpCode() {
        return httpCode;
    }
}
