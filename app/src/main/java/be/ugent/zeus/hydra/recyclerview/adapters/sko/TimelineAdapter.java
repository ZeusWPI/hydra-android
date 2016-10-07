package be.ugent.zeus.hydra.recyclerview.adapters.sko;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.sko.TimelinePost;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.sko.TimelineViewHolder;
import be.ugent.zeus.hydra.utils.customtabs.ActivityHelper;

/**
 * @author Niko Strijbol
 */
public class TimelineAdapter extends ItemAdapter<TimelinePost, TimelineViewHolder> {

    private final ActivityHelper helper;

    public TimelineAdapter(ActivityHelper helper) {
        this.helper = helper;
    }

    public ActivityHelper getHelper() {
        return helper;
    }

    @Override
    public TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sko_timeline_post, parent, false);
        return new TimelineViewHolder(v, this);
    }
}