package be.ugent.zeus.hydra.recyclerview.adapters.minerva;

import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.minerva.CourseViewHolder;
import be.ugent.zeus.hydra.utils.ViewUtils;

/**
 * @author Niko Strijbol
 */
public class CourseAdapter extends ItemAdapter<Course, CourseViewHolder> {

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CourseViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_course));
    }
}