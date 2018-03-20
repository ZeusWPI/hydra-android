package be.ugent.zeus.hydra.common.network;

import java.io.IOException;

/**
 * @author Niko Strijbol
 */
public class UnsuccessfulRequestException extends IOException {

    private final int httpCode;

    public UnsuccessfulRequestException(int httpCode) {
        super("Unsuccessful HTTP request, response code is " + httpCode);
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
