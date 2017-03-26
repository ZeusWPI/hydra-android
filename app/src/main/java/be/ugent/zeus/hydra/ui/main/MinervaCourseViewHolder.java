package be.ugent.zeus.hydra.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.minerva.overview.CourseActivity;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.ui.common.recyclerview.DataViewHolder;
import be.ugent.zeus.hydra.ui.common.html.Utils;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
class MinervaCourseViewHolder extends DataViewHolder<Course> {

    private TextView name;
    private TextView subtitle;
    private View parent;

    MinervaCourseViewHolder(View itemView) {
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
        parent.setOnClickListener(view -> CourseActivity.start(view.getContext(), course));
    }
}