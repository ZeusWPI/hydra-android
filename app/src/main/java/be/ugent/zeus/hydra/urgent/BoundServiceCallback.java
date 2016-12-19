package be.ugent.zeus.hydra.urgent;

/**
 * Interface that objects that have a bound service should implement. This allows the service to request to be unbound,
 * allowing a bound service to kill itself.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface BoundServiceCallback {

    /**
     * Request that the service be unbound. This can also be used as a callback for scenarios where the service
     * requests that it gets unbound. This will thus obviously not be called when the service gets unbound due to
     * external factors, such as the Activity or Fragment being destroyed.
     */
    void requestUnbind();
}