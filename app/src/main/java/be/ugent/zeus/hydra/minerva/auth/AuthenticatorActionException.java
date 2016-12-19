package be.ugent.zeus.hydra.minerva.auth;

import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;

/**
 * Thrown when user action is required, e.g. the refresh token has expired.
 *
 * The thrower of this exception should, if possible, make the bundle returned by the {@link android.accounts.AccountManager}
 * accessible to the catcher of this exception.
 *
 * @author Niko Strijbol
 */
public class AuthenticatorActionException extends RequestFailureException {

    public AuthenticatorActionException() {
        super();
    }
}