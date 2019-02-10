package be.ugent.zeus.hydra.minerva.course.list;

import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.recyclerview.ResultStarter;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.AdapterUpdate;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.ListUpdateCallback;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.SearchableAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.ordering.ItemDragHelperAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.ordering.OnStartDragListener;
import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.course.CourseRepository;
import java9.util.stream.Collectors;
import java9.util.stream.IntStream;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Adapts a list of courses.
 *
 * @author Niko Strijbol
 */
class MinervaCourseAdapter extends SearchableAdapter<Pair<Course, Long>, MinervaCourseViewHolder> implements ItemDragHelperAdapter {

    private CourseRepository courseDao;
    private final OnStartDragListener startDragListener;
    private final ResultStarter resultStarter;

    MinervaCourseAdapter(OnStartDragListener startDragListener, ResultStarter resultStarter) {
        super(c -> c.first.getTitle().toLowerCase());
        this.startDragListener = startDragListener;
        this.resultStarter = resultStarter;
    }

    /**
     * @param courseDao The course dao.
     */
    public void setCourseDao(CourseRepository courseDao) {
        this.courseDao = courseDao;
    }

    @NonNull
    @Override
    public MinervaCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MinervaCourseViewHolder(ViewUtils.inflate(parent, R.layout.item_minerva_course), startDragListener, this, resultStarter);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        AdapterUpdate<Pair<Course, Long>> update = new AdapterUpdate<Pair<Course, Long>>() {
            @Override
            public List<Pair<Course, Long>> getNewData(List<Pair<Course, Long>> existingData) {
                Collections.swap(existingData, fromPosition, toPosition);
                return existingData;
            }

            @Override
            public void applyUpdatesTo(ListUpdateCallback listUpdateCallback) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public boolean shouldUseMultiThreading() {
                return false; // Multithreading causes problems.
            }
        };
        dataContainer.submitUpdate(update);
        return true;
    }

    @Override
    public void onMoveCompleted(RecyclerView.ViewHolder viewHolder) {
        if (courseDao == null) {
            throw new IllegalStateException("The course DAO cannot be null!");
        }
        AsyncTask.execute(() -> {
            Collection<Course> courses = IntStream.range(0, getItemCount())
                    .mapToObj(value -> {
                        Course course1 = getItem(value).first;
                        course1.setOrder(value);
                        return course1;
                    })
                    .collect(Collectors.toList());
            courseDao.update(courses);
        });
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return !this.isSearching();
    }
}