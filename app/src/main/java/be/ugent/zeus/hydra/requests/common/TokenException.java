package be.ugent.zeus.hydra.requests.common;

/**
 * Exception thrown when something went wrong while getting a token.
 *
 * @author Niko Strijbol
 */
public class TokenException extends Exception {

    public TokenException() {
        super();
    }

    public TokenException(Throwable cause) {
        super(cause);
    }
}
