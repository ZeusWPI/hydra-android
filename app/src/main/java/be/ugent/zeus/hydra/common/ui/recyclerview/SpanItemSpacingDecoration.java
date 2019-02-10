package be.ugent.zeus.hydra.common.ui.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;
import be.ugent.zeus.hydra.R;

/**
 * Add spacing to elements for a staggered grid. This will add {@link #spacing} to the left and right of every element.
 *
 * If the layout manager is not a {@link StaggeredGridLayoutManager} or {@link GridLayoutManager}, this will do nothing.
 *
 * @author Niko Strijbol
 */
public class SpanItemSpacingDecoration extends RecyclerView.ItemDecoration {

    private final int spacing;

    @SuppressWarnings("WeakerAccess")
    public SpanItemSpacingDecoration(int spacing) {
        this.spacing = spacing;
    }

    /**
     * Initialize the decoration using the default spacing.
     */
    public SpanItemSpacingDecoration(Context context) {
        this(context.getResources().getDimensionPixelSize(R.dimen.card_margin_big_component));
    }

    @Override
    public void getItemOffsets(Rect outRect, final View view, final RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        //If this is not supported, do nothing. Otherwise extract the span.
        if (!(parent.getLayoutManager() instanceof StaggeredGridLayoutManager || parent.getLayoutManager() instanceof GridLayoutManager)) {
            return;
        }

        outRect.right = spacing;
        outRect.left = spacing;
    }
}