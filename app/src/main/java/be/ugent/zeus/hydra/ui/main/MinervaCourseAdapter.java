package be.ugent.zeus.hydra.ui.main;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.DiffSearchableItemAdapter;
import be.ugent.zeus.hydra.ui.common.ViewUtils;

/**
 * Adapts a list of courses.
 *
 * @author Niko Strijbol
 */
class MinervaCourseAdapter extends DiffSearchableItemAdapter<Course, MinervaCourseViewHolder> {

    MinervaCourseAdapter() {
        super(c -> c.getTitle().toLowerCase());
    }

    @Override
    public MinervaCourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MinervaCourseViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_course));
    }
}