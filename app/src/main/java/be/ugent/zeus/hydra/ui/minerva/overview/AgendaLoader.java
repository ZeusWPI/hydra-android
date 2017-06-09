package be.ugent.zeus.hydra.ui.minerva.overview;

import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.database.minerva.AgendaDao;
import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.ui.common.loaders.LoaderException;
import be.ugent.zeus.hydra.ui.common.loaders.BroadcastLoader;

import java.util.List;

/**
 * @author Niko Strijbol
 */
@Deprecated
class AgendaLoader extends BroadcastLoader<List<AgendaItem>> {

    private AgendaDao dao;
    private Course course;

    /**
     * This loader has the option to ignore the cache.
     *
     * @param context The context.
     */
    AgendaLoader(Context context, AgendaDao dao, Course course) {
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
        return dao.getAgendaForCourse(course, false, true);
    }
}