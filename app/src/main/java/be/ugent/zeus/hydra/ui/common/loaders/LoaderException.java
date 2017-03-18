package be.ugent.zeus.hydra.ui.common.loaders;

/**
 * Exception thrown by {@link AbstractLoader} if the data cannot be loaded.
 *
 * @author Niko Strijbol
 */
public class LoaderException extends Exception {

    public LoaderException(String message) {
        super(message);
    }

    public LoaderException(Throwable cause) {
        super(cause);
    }
}