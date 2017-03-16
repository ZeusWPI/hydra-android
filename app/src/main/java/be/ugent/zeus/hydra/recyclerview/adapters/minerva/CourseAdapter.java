package be.ugent.zeus.hydra.recyclerview.adapters.minerva;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.recyclerview.adapters.common.DiffSearchableItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.minerva.CourseViewHolder;
import be.ugent.zeus.hydra.utils.ViewUtils;

/**
 * Adapts a list of courses.
 *
 * @author Niko Strijbol
 */
public class CourseAdapter extends DiffSearchableItemAdapter<Course, CourseViewHolder> {

    public CourseAdapter() {
        super(c -> c.getTitle().toLowerCase());
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CourseViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_course));
    }
}