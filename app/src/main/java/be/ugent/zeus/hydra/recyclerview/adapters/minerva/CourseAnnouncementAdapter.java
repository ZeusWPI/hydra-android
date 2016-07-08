package be.ugent.zeus.hydra.recyclerview.adapters.minerva;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.minerva.AnnouncementActivity;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.CourseWrapper;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.html.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * TODO: try to remove raw list
 *
 * @author Niko Strijbol
 */
public class CourseAnnouncementAdapter extends RecyclerView.Adapter<CourseAnnouncementAdapter.ViewHolder> {

    private static final int COURSE = 1;
    private static final int ANNOUNCEMENT = 2;

    private List data = Collections.emptyList();

    private List<CourseWrapper> items = Collections.emptyList();
    private HydraApplication application;

    public CourseAnnouncementAdapter(HydraApplication application) {
        this.application = application;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case COURSE:
                return new CourseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minerva_course, parent, false));
            case ANNOUNCEMENT:
                return new AnnouncementViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minerva_announcement, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.populateData(data.get(position));
    }

    /**
     * Set the items. This use the provided items. If you use collections and want a new collection, you should do
     *
     *
     * This will also call {@link #notifyDataSetChanged()}.
     *
     * @param list The new elements.
     */
    public void setItems(List<Course> list) {
        data = new ArrayList<>();

        items = new ArrayList<>();
        for (Course c : list) {
            CourseWrapper wrapper = new CourseWrapper(c, application);
            wrapper.loadAnnouncements();
            data.add(c);
            items.add(wrapper);
        }

        notifyDataSetChanged();
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

    public static abstract class ViewHolder<D> extends RecyclerView.ViewHolder implements DataViewHolder<D> {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * This is not a static class because we need access to the adapter.
     *
     * TODO: Investigate if this leaks memory.
     */
    public class CourseViewHolder extends ViewHolder<Course> {
        private TextView name;
        private TextView subtitle;
        private View parent;

        public CourseViewHolder(View itemView) {
            super(itemView);

            name = $(itemView, R.id.name);
            subtitle = $(itemView, R.id.subtitle);
            parent = $(itemView, R.id.parent_layout);
        }

        /**
         * Populate with the data. This method must be called in {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder,
         * int)}
         *
         * @param course The data.
         */
        @Override
        public void populateData(final Course course) {
            name.setText(course.getTitle());
            final CharSequence tutor = course.getTutorName() == null ? "" : Utils.fromHtml(course.getTutorName());
            subtitle.setText(tutor + " - " + course.getCode());
            parent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    CourseWrapper wrapper = getWrapper(course);
                    if(wrapper.getAnnouncements().size() > 0) {
                        int position = data.indexOf(course);
                        if(isExpanded(position)) {
                            data.removeAll(wrapper.getAnnouncements());
                            notifyItemRangeRemoved(position + 1, wrapper.getAnnouncements().size());
                        } else {
                            data.addAll(position + 1, wrapper.getAnnouncements());
                            notifyItemRangeInserted(position + 1, wrapper.getAnnouncements().size());
                        }

                    }
                }
            });
        }
    }

    private boolean isExpanded(int position) {
        return position + 1 < data.size() && data.get(position + 1) instanceof Announcement;
    }

    public static class AnnouncementViewHolder extends ViewHolder<Announcement> {

        public static final String PARCEL_NAME = "announcement_view";

        private TextView title;
        private TextView subtitle;
        private View parent;

        public AnnouncementViewHolder(View itemView) {
            super(itemView);
            title = $(itemView, R.id.title);
            parent = $(itemView, R.id.parent_layout);
            subtitle = $(itemView, R.id.subtitle);
        }

        @Override
        public void populateData(final Announcement data) {
            title.setText(data.getTitle());
            String infoText = String.format(new Locale("nl"), "%s door %s",
                    DateUtils.relativeDateString(data.getDate(), itemView.getContext()),
                    data.getLecturer());
            subtitle.setText(infoText);

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), AnnouncementActivity.class);
                    intent.putExtra(PARCEL_NAME, (Parcelable) data);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    private CourseWrapper getWrapper(Course c) {
        //TODO: make this efficient
        for (CourseWrapper w : this.items) {
            if (w.getCourse() == c) {
                return w;
            }
        }

        throw new IllegalStateException("This course does not exist.");
    }
}
