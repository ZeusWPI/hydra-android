package be.ugent.zeus.hydra.ui.main.minerva;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.recyclerview.ordering.OnStartDragListener;
import be.ugent.zeus.hydra.ui.minerva.overview.CourseActivity;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.common.html.Utils;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
class MinervaCourseViewHolder extends DataViewHolder<Course> {

    private final TextView name;
    private final TextView subtitle;

    MinervaCourseViewHolder(View itemView, OnStartDragListener listener) {
        super(itemView);

        name = $(itemView, R.id.name);
        subtitle = $(itemView, R.id.subtitle);
        ImageView dragHandle = $(itemView, R.id.drag_handle);
        dragHandle.setOnTouchListener((v, event) -> {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                listener.onStartDrag(MinervaCourseViewHolder.this);
                return true;
            }
            return false;
        });
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
        itemView.setOnClickListener(view -> CourseActivity.start(view.getContext(), course));
    }
}