package be.ugent.zeus.hydra.utils.recycler;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Simple item decorator that add space between elements in a {@link LinearLayoutManager}. If it is not a linear layout
 * manager, this will do nothing.
 *
 * If the layout manager is in vertical mode, the space will be added to the bottom of the items.
 * If the mode is horizontal, the spacing will be applied to the right of the items.
 *
 * The last item will not get spacing.
 *
 * @author Niko Strijbol
 */
public class ItemSpacingDecoration extends RecyclerView.ItemDecoration {

    private final int spacing;

    public ItemSpacingDecoration(int mVerticalSpaceHeight) {
        this.spacing = mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        //If this is not a LinearLayoutManager, do nothing.
        if (!(parent.getLayoutManager() instanceof LinearLayoutManager)) {
            return;
        }

        LinearLayoutManager manager = (LinearLayoutManager) parent.getLayoutManager();

        //If it is the last one, no spacing!
        if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
            return;
        }

        if (manager.getOrientation() == LinearLayoutManager.VERTICAL) {
            outRect.bottom = spacing;
        } else {
            outRect.right = spacing;
        }
    }
}