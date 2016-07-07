package be.ugent.zeus.hydra.recyclerview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.utils.html.Utils;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class CourseViewHolder extends RecyclerView.ViewHolder implements DataViewHolder<Course> {

    private TextView name;
    private TextView subtitle;

    public CourseViewHolder(View itemView) {
        super(itemView);

        name = $(itemView, R.id.name);
        subtitle = $(itemView, R.id.subtitle);
    }

    /**
     * Populate with the data. This method must be called in {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder,
     * int)}
     *
     * @param course The data.
     */
    @Override
    public void populateData(Course course) {
        name.setText(course.getTitle());
        //TODO this
        CharSequence tutor = course.getTutorName() == null ? "" : Utils.fromHtml(course.getTutorName());
        subtitle.setText(tutor + " - " + course.getCode());

    }
}
