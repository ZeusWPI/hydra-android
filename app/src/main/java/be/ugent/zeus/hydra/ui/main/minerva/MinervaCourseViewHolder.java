package be.ugent.zeus.hydra.ui.main.minerva;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.ui.common.html.Utils;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.ui.common.recyclerview.ordering.OnStartDragListener;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.minerva.overview.CourseActivity;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
class MinervaCourseViewHolder extends DataViewHolder<Pair<Course, Integer>> {

    private final TextView name;
    private final TextView subtitle;
    private final ImageView unreadCount;
    private final ResultStarter resultStarter;

    MinervaCourseViewHolder(View itemView, OnStartDragListener listener, DragHelper dragHelper, ResultStarter starter) {
        super(itemView);
        this.resultStarter = starter;
        name = $(itemView, R.id.name);
        subtitle = $(itemView, R.id.subtitle);
        ImageView dragHandle = $(itemView, R.id.drag_handle);
        dragHandle.setOnTouchListener((v, event) -> {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN && dragHelper.isDragEnabled()) {
                listener.onStartDrag(MinervaCourseViewHolder.this);
                return true;
            }
            return false;
        });
        unreadCount = $(itemView, R.id.unread_icon);
    }

    /**
     * Populate with the data. This method must be called in {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder,
     * int)}
     *
     * @param data The data.
     */
    @Override
    public void populate(final Pair<Course, Integer> data) {
        Course course = data.first;
        name.setText(course.getTitle());
        final CharSequence tutor = Utils.fromHtml(course.getTutorName());
        subtitle.setText(tutor + " - " + course.getCode());

        //Set onclick listener
        itemView.setOnClickListener(view -> CourseActivity.startForResult(resultStarter, course, CourseActivity.Tab.ANNOUNCEMENTS));

        if (data.second > 0) {
            unreadCount.setVisibility(View.VISIBLE);
        } else {
            unreadCount.setVisibility(View.GONE);
        }
    }
}