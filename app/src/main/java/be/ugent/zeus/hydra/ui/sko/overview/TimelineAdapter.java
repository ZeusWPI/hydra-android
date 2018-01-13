package be.ugent.zeus.hydra.ui.sko.overview;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.sko.TimelinePost;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.ItemDiffAdapter;

/**
 * @author Niko Strijbol
 */
class TimelineAdapter extends ItemDiffAdapter<TimelinePost, TimelineViewHolder> {

    private final ActivityHelper helper;

    TimelineAdapter(ActivityHelper helper) {
        super();
        this.helper = helper;
    }

    public ActivityHelper getHelper() {
        return helper;
    }

    @Override
    public TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TimelineViewHolder(ViewUtils.inflate(parent, R.layout.item_sko_timeline_post), this);
    }
}