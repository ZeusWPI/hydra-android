package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

/**
 * @author Niko Strijbol
 */
interface ListUpdateCallback extends android.support.v7.util.ListUpdateCallback {

    /**
     * Called when the data has changed, but no information about the change is known.
     */
    void onDataSetChanged();
}