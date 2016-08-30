package be.ugent.zeus.hydra.recyclerview.viewholder.minerva;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.minerva.CourseActivity;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.recyclerview.viewholder.DataViewHolder;
import be.ugent.zeus.hydra.utils.html.Utils;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class CourseViewHolder extends DataViewHolder<Course> {

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
    public void populate(final Course course) {
        name.setText(course.getTitle());
        final CharSequence tutor = Utils.fromHtml(course.getTutorName());
        subtitle.setText(tutor + " - " + course.getCode());


        //Set onclick listener
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CourseActivity.start(itemView.getContext(), course);
            }
        });
    }
}