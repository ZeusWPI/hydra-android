package com.mylovemhz.simplay;

/**
 * Interface that objects that have a bound service should implement. This allows the service to request to be unbound,
 * allowing a bound service to kill itself.
 *
 * @author Niko Strijbol
 */
public interface BoundServiceCallback {

    /**
     * This is called by the service to know whether the service can be unbound or not.
     *
     * @return True if the service will be unbound.
     */
    boolean canUnbind();

    /**
     * Request that the service be unbound. It is up to the implementation to decide if it wants to unbind or not.
     *
     * @return True if the service was unbound, otherwise false.
     */
    boolean requestUnbind();
}