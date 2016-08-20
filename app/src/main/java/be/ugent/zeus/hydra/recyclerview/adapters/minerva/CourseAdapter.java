package be.ugent.zeus.hydra.recyclerview.adapters.minerva;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.minerva.CourseViewHolder;

/**
 * @author Niko Strijbol
 * @version 5/07/2016
 */
public class CourseAdapter extends ItemAdapter<Course, CourseViewHolder> {

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CourseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minerva_course, parent, false));
    }
}