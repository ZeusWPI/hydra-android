package be.ugent.zeus.hydra.ui.main.minerva;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.database.minerva.CourseDao;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.DiffSearchableItemAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.ordering.ItemDragHelperAdapter;
import be.ugent.zeus.hydra.ui.common.recyclerview.ordering.OnStartDragListener;
import java8.util.stream.Collectors;
import java8.util.stream.IntStreams;

import java.util.Collection;
import java.util.Collections;

/**
 * Adapts a list of courses.
 *
 * @author Niko Strijbol
 */
class MinervaCourseAdapter extends DiffSearchableItemAdapter<Course, MinervaCourseViewHolder> implements ItemDragHelperAdapter, DragHelper {

    private CourseDao courseDao;
    private final OnStartDragListener startDragListener;

    MinervaCourseAdapter(OnStartDragListener startDragListener) {
        super(c -> c.getTitle().toLowerCase());
        this.startDragListener = startDragListener;
    }

    /**
     * @param courseDao The course dao.
     */
    public void setCourseDao(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    @Override
    public MinervaCourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MinervaCourseViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_course), startDragListener, this);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(items, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onMoveCompleted(RecyclerView.ViewHolder viewHolder) {
        if (courseDao == null) {
            throw new IllegalStateException("The course DAO cannot be null!");
        }
        AsyncTask.execute(() -> {
            Collection<Course> courses = IntStreams.range(0, getItemCount())
                    .mapToObj(value -> {
                        Course course1 = items.get(value);
                        course1.setOrder(value);
                        return course1;
                    })
                    .collect(Collectors.toList());
            courseDao.update(courses, true);
        });
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return isDragEnabled();
    }

    @Override
    public boolean isDragEnabled() {
        return !isSearching();
    }
}