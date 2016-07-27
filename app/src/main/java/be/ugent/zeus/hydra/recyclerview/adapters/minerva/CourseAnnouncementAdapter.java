package be.ugent.zeus.hydra.recyclerview.adapters.minerva;

import java.util.*;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.CourseWrapper;
import be.ugent.zeus.hydra.recyclerview.viewholder.AbstractViewHolder;
import be.ugent.zeus.hydra.recyclerview.viewholder.minerva.AnnouncementViewHolder;
import be.ugent.zeus.hydra.recyclerview.viewholder.minerva.CourseViewHolder;

/**
 * Adapter for a list that has courses and where those courses have child items, announcements.
 *
 * @author Niko Strijbol
 */
public class CourseAnnouncementAdapter extends RecyclerView.Adapter<AbstractViewHolder> {

    private static final int COURSE = 1;
    private static final int ANNOUNCEMENT = 2;

    //The data, in order it appears in the list.
    private List<Object> data = Collections.emptyList();
    //Map the courses to their wrapper.
    private Map<String, CourseWrapper> wrapperMap = new HashMap<>();

    private HydraApplication application;

    public CourseAnnouncementAdapter(HydraApplication application) {
        this.application = application;
    }

    @Override
    public AbstractViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case COURSE:
                return new CourseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minerva_course, parent, false), this);
            case ANNOUNCEMENT:
                return new AnnouncementViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minerva_announcement, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(AbstractViewHolder holder, int position) {
        holder.populate(data.get(position));
    }

    @Override
    public void onViewRecycled(AbstractViewHolder holder) {
        if(holder instanceof CourseViewHolder && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
            Course c = (Course) data.get(holder.getAdapterPosition());
            CourseWrapper w = getWrapper(c);
            w.cancelLoading();
        }
    }

    /**
     * Set the items. This use the provided items.
     *
     * This will also call {@link #notifyDataSetChanged()}.
     *
     * @param list The new elements.
     */
    public void setItems(List<Course> list) {
        data = new ArrayList<>(list.size());
        data.addAll(list);

        notifyDataSetChanged();
    }

    public void clear() {
        int size = getItemCount();
        data.clear();
        for(Map.Entry<String, CourseWrapper> e: wrapperMap.entrySet()) {
            e.getValue().cancelLoading();
        }
        wrapperMap.clear();
        notifyItemRangeRemoved(0, size);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(data.get(position) instanceof Course) {
            return COURSE;
        } else {
            return ANNOUNCEMENT;
        }
    }

    public boolean isExpanded(int position) {
        return position + 1 < data.size() && data.get(position + 1) instanceof Announcement;
    }

    public List<Object> getData() {
        return data;
    }

    /**
     * Get the wrapper for a course.
     *
     * The list of course wrappers is also lazy: it only makes one when needed.
     *
     * @param c The course to get a wrapper for.
     *
     * @return The CourseWrapper.
     */
    public CourseWrapper getWrapper(Course c) {
        CourseWrapper w = wrapperMap.get(c.getId());

        if(w == null) {
            CourseWrapper wrapper = new CourseWrapper(c, application);
            wrapperMap.put(c.getId(), wrapper);
            w = wrapper;
        }

        return w;
    }
}