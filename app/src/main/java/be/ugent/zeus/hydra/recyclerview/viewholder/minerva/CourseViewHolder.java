package be.ugent.zeus.hydra.recyclerview.viewholder.minerva;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.CourseWrapper;
import be.ugent.zeus.hydra.recyclerview.adapters.minerva.CourseAnnouncementAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.AbstractViewHolder;
import be.ugent.zeus.hydra.requests.common.RequestExecutor;
import be.ugent.zeus.hydra.utils.html.Utils;

import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 * @version 15/07/2016
 */
public class CourseViewHolder extends AbstractViewHolder<Course> {

    private TextView name;
    private TextView subtitle;
    private ProgressBar progressBar;
    private ImageView arrow;
    private View parent;

    private CourseAnnouncementAdapter adapter;

    public CourseViewHolder(View itemView, CourseAnnouncementAdapter adapter) {
        super(itemView);

        name = $(itemView, R.id.name);
        subtitle = $(itemView, R.id.subtitle);
        parent = $(itemView, R.id.parent_layout);
        progressBar = $(itemView, R.id.progress_bar);
        arrow = $(itemView, R.id.arrow_icon);

        this.adapter = adapter;
    }

    /**
     * Populate with the data.
     *
     * @param course The data.
     */
    @Override
    public void populate(final Course course) {

        name.setText(course.getTitle());
        final CharSequence tutor = Utils.fromHtml(course.getTutorName());
        subtitle.setText(tutor + " - " + course.getCode());

        arrow.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        final CourseWrapper wrapper = adapter.getWrapper(course);
        wrapper.loadAnnouncements(new RequestExecutor.Callback<List<Announcement>>() {
            @Override
            public void receiveData(@NonNull List<Announcement> data) {
                //Hide progress bar
                progressBar.setVisibility(View.GONE);

                //If there is data, show icon
                if(!data.isEmpty()) {
                    arrow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void receiveError(@NonNull Throwable e) {
                //Hide progress bar.
                progressBar.setVisibility(View.GONE);
            }
        });

        //Set onclick listener
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wrapper.getAnnouncements().size() > 0) {
                    int position = adapter.getData().indexOf(course);
                    if(adapter.isExpanded(position)) {
                        adapter.getData().removeAll(wrapper.getAnnouncements());
                        adapter.notifyItemRangeRemoved(position + 1, wrapper.getAnnouncements().size());
                        toggleIcon(true);
                    } else {
                        adapter.getData().addAll(position + 1, wrapper.getAnnouncements());
                        adapter.notifyItemRangeInserted(position + 1, wrapper.getAnnouncements().size());
                        toggleIcon(false);
                    }
                }
            }
        });
    }

    private void toggleIcon(boolean expanded) {
        if(expanded) {
            arrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        } else {
            arrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        }
    }
}