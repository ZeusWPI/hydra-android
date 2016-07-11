package be.ugent.android.sdk.oauth.event;

/**
 * Interface for the object that handles the visual part of logging in.
 * This is often an {@link android.app.Activity} or {@link android.app.Fragment}, but it really doesn't matter
 * what it is.
 *
 * @author Niko Strijbol
 */
public interface AuthorizationCodeEventHandler extends AuthorizationEventHandler {

    /**
     * If the authorization was successful, an authorization code is given. With this code you can request a
     * {@link be.ugent.android.sdk.oauth.json.BearerToken}.
     *
     * @param authorizationCode The authorization code.
     */
    void receiveAuthorizationCode(String authorizationCode);
}