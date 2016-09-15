package be.ugent.zeus.hydra.recyclerview.adapters.sko;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.sko.TimelinePost;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.sko.TimelineViewHolder;
import be.ugent.zeus.hydra.utils.customtabs.ActivityHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Niko Strijbol
 */
public class TimelineAdapter extends ItemAdapter<TimelinePost, TimelineViewHolder> {

    private final Set<TimelinePost> timelinePosts = new HashSet<>();
    private final Context context;
    private final ActivityHelper helper;

    public TimelineAdapter(Context context, ActivityHelper helper) {
        this.context = context;
        this.helper = helper;
    }

    public void setExpanded(TimelinePost post) {
        timelinePosts.add(post);
    }

    public void setNotExpanded(TimelinePost post) {
        timelinePosts.remove(post);
    }

    public boolean isExpanded(TimelinePost post) {
        return timelinePosts.contains(post);
    }

    public Context getContext() {
        return context;
    }

    public ActivityHelper getHelper() {
        return helper;
    }

    @Override
    public void setItems(List<TimelinePost> list) {
        super.setItems(list);
        timelinePosts.clear();
    }

    @Override
    public TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sko_timeline_post, parent, false);
        return new TimelineViewHolder(v, this);
    }
}
