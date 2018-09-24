package be.ugent.zeus.hydra.minerva.calendar.sync;

/**
 * Thrown when there is no calendar provider on the device, or we cannot access it for some reason.
 *
 * @author Niko Strijbol
 */
public class MissingCalendarException extends Exception {

    public MissingCalendarException(Exception cause) {
        super(cause);
    }
}
