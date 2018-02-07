package be.ugent.zeus.hydra.ui.main.homefeed;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * This callback will enable swiping right to dismiss view holders, only if they implement {@link
 * SwipeDismissableViewHolder}.
 *
 * @author Niko Strijbol
 */
public class DismissCallback extends ItemTouchHelper.Callback {

    private static final String TAG = "DismissCallback";

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof SwipeDismissableViewHolder && ((SwipeDismissableViewHolder) viewHolder).isSwipeEnabled()) {
            return makeMovementFlags(0, ItemTouchHelper.RIGHT);
        } else {
            return 0;
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof SwipeDismissableViewHolder) {
            ((SwipeDismissableViewHolder) viewHolder).onSwiped();
        } else {
            // TODO: if this behaviour is verified, throw exception instead.
            Log.w(TAG, "Swiped unswipeable card! Ignoring.");
        }
    }
}