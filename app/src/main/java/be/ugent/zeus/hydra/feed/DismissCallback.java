package be.ugent.zeus.hydra.feed;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This callback will enable swiping right to dismiss view holders, only if they implement {@link
 * SwipeDismissableViewHolder}.
 *
 * @author Niko Strijbol
 */
public class DismissCallback extends ItemTouchHelper.Callback {

    private static final String TAG = "DismissCallback";

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof SwipeDismissableViewHolder && ((SwipeDismissableViewHolder) viewHolder).isSwipeEnabled()) {
            return makeMovementFlags(0, ItemTouchHelper.RIGHT);
        } else {
            return 0;
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof SwipeDismissableViewHolder) {
            ((SwipeDismissableViewHolder) viewHolder).onSwiped();
        } else {
            Log.w(TAG, "Swiped unswipeable card! Ignoring.");
        }
    }
}
