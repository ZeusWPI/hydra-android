package be.ugent.zeus.hydra.ui.common;

/**
 * Thrown when information for a non-existent tab is requested.
 *
 * @author Niko Strijbol
 */
public class IllegalTabException extends IllegalStateException {

    public IllegalTabException(int position, int count) {
        super("Request tab at position " + position + ", but only " + count + " tabs are available.");
    }
}