package be.ugent.zeus.hydra.utils.recycler;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * @author Niko Strijbol
 */
public abstract class LinearItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * Check if the orientation is vertical.
     *
     * @param parent The parent.
     *
     * @return If the orientation is vertical, true is returned.
     */
    protected boolean isVertical(RecyclerView parent) {
        if(parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) parent.getLayoutManager();
            return manager.getOrientation() == LinearLayoutManager.VERTICAL;
        } else {
            throw new IllegalStateException("DividerItemDecoration can only be used with a LinearLayoutManager.");
        }
    }
}
