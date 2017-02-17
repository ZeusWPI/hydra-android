package be.ugent.zeus.hydra.minerva.agenda;

import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loaders.BroadcastLoader;
import be.ugent.zeus.hydra.loaders.LoaderException;
import be.ugent.zeus.hydra.minerva.sync.SyncBroadcast;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.models.minerva.Course;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class AgendaDaoLoader extends BroadcastLoader<List<AgendaItem>> {

    private AgendaDao dao;
    private Course course;

    /**
     * This loader has the option to ignore the cache.
     *
     * @param context The context.
     */
    public AgendaDaoLoader(Context context, AgendaDao dao, Course course) {
        super(context, new IntentFilter(SyncBroadcast.SYNC_AGENDA));
        this.dao = dao;
        this.course = course;
    }

    /**
     * Provide the data for the loader.
     *
     * @return The data.
     *
     * @throws LoaderException If the data could not be provided.
     */
    @NonNull
    @Override
    protected List<AgendaItem> loadData() throws LoaderException {
        return dao.getAgendaForCourse(course, false);
    }
}