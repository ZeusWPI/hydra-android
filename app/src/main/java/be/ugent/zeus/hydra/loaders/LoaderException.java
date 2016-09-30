package be.ugent.zeus.hydra.loaders;

/**
 * Exception thrown by {@link AbstractAsyncLoader} if the data cannot be loaded.
 *
 * @author Niko Strijbol
 */
public class LoaderException extends Exception {

    public LoaderException(Throwable cause) {
        super(cause);
    }
}