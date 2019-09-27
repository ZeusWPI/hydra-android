package be.ugent.zeus.hydra.feed;

/**
 * A view holder for things that are dismissable by swipe.
 * @author Niko Strijbol
 */
public interface SwipeDismissableViewHolder {

    /**
     * Called when swiping has happened.
     */
    void onSwiped();

    /**
     * @return If swiping should be allowed.
     */
    boolean isSwipeEnabled();
}
