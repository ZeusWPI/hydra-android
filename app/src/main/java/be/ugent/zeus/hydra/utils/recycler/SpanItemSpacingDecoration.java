package be.ugent.zeus.hydra.utils.recycler;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import be.ugent.zeus.hydra.R;

/**
 * Add spacing to elements for a staggered grid. This will add spacing on the right of every element, except for the
 * last span column.
 *
 * If the layout manager is not a {@link StaggeredGridLayoutManager}, this will do nothing.
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
        this((int) context.getResources().getDimension(R.dimen.content_spacing));
    }

    @Override
    public void getItemOffsets(Rect outRect, final View view, final RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        //If this is not a LinearLayoutManager, do nothing.
        if (!(parent.getLayoutManager() instanceof StaggeredGridLayoutManager)) {
            return;
        }

        StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) parent.getLayoutManager();
        int spanIndex = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();

        //If this is the last span, do nothing.
        if (spanIndex < manager.getSpanCount() - 1) {
            outRect.right = spacing;
        }
    }
}