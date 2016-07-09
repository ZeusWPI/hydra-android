package be.ugent.android.sdk.oauth.event;

/**
 * Interface for the object that handles the visual part of logging in.
 * This is often an {@link android.app.Activity} or {@link android.app.Fragment}, but it really doesn't matter
 * what it is.
 *
 * @author Niko Strijbol
 */
public interface AuthorizationEventHandler {

    /**
     * Receive authorization events.
     *
     * Note: the success event is not fired by the {@link be.ugent.android.sdk.oauth.AuthorizationManager}.
     *
     * @param event The event.
     */
    void onAuthorizationEvent(AuthorizationEvent event);
}