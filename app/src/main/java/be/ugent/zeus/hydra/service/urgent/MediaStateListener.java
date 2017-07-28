package be.ugent.zeus.hydra.service.urgent;

/**
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface MediaStateListener {

    void onStateChanged(@MediaState int oldState, @MediaState int newState);

}
