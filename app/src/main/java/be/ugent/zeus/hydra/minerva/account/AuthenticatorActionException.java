package be.ugent.zeus.hydra.minerva.account;

import be.ugent.zeus.hydra.repository.requests.RequestException;

/**
 * Thrown when user action is required, e.g. the refresh token has expired.
 *
 * The thrower of this exception should, if possible, make the bundle returned by the {@link android.accounts.AccountManager}
 * accessible to the catcher of this exception.
 *
 * @author Niko Strijbol
 */
public class AuthenticatorActionException extends RequestException {

    public AuthenticatorActionException() {
        super();
    }
}