package be.ugent.zeus.hydra.utils.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Niko Strijbol
 * @version 22/07/2016
 */
public class SpacingItemDecoration extends LinearItemDecoration {

    private final int spacing;

    public SpacingItemDecoration(int mVerticalSpaceHeight) {
        this.spacing = mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        //If it is the last one, no spacing!
        if(parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
            return;
        }

        if(isVertical(parent)) {
            outRect.bottom = spacing;
        } else {
            outRect.right = spacing;
        }
    }
}