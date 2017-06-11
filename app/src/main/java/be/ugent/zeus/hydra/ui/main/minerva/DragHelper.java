package be.ugent.zeus.hydra.ui.main.minerva;

/**
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface DragHelper {

    /**
     * @return True if dragging is allowed, otherwise false.
     */
    boolean isDragEnabled();
}