package be.ugent.zeus.hydra.ui.sko.overview;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.sko.TimelinePost;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;

/**
 * @author Niko Strijbol
 */
public class TimelineAdapter extends be.ugent.zeus.hydra.ui.common.recyclerview.adapters.DiffAdapter<TimelinePost, TimelineViewHolder> {

    private final ActivityHelper helper;

    public TimelineAdapter(ActivityHelper helper) {
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