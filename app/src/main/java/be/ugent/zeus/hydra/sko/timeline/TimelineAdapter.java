package be.ugent.zeus.hydra.sko.timeline;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.ItemDiffAdapter;

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

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TimelineViewHolder(ViewUtils.inflate(parent, R.layout.item_sko_timeline_post), this);
    }
}