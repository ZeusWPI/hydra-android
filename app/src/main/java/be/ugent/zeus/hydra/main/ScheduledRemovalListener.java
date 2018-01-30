package be.ugent.zeus.hydra.main;

/**
 * Used by the {@link MainActivity} to notify fragments they will be removed. At this point, a fragment should consider
 * itself removed, even if it is not yet removed.
 *
 * This allows fragments to do things like dismiss snackbars, or close action modes.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface ScheduledRemovalListener {

    /**
     * Called when the user will be switching to another fragment.
     */
    void onRemovalScheduled();
}