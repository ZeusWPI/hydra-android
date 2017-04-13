package be.ugent.zeus.hydra.ui.minerva.overview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.database.minerva.AnnouncementDao;
import be.ugent.zeus.hydra.data.database.minerva.DatabaseBroadcaster;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.ui.common.loaders.BroadcastListener;
import be.ugent.zeus.hydra.ui.common.loaders.LoaderException;
import be.ugent.zeus.hydra.ui.common.loaders.BroadcastLoader;
import java8.util.function.BiPredicate;
import java8.util.function.BiPredicates;

import java.util.List;

/**
 * Load a list of {@link Announcement}s for a {@link Course}.
 *
 * @author Niko Strijbol
 */
class AnnouncementsLoader extends BroadcastLoader<List<Announcement>> {

    private final AnnouncementDao dao;
    private final Course course;

    /**
     * This loader has the option to ignore the cache.
     *
     * @param context The context.
     */
    AnnouncementsLoader(Context context, AnnouncementDao dao, Course course) {
        super(context, getFilter());
        this.dao = dao;
        this.course = course;
    }

    private static IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SyncBroadcast.SYNC_PROGRESS_WHATS_NEW);
        filter.addAction(DatabaseBroadcaster.MINERVA_ANNOUNCEMENT_UPDATED);
        return filter;
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
    protected List<Announcement> loadData() throws LoaderException {
        return dao.getAnnouncementsForCourse(course, true);
    }

    @Override
    protected BroadcastReceiver getReceiver() {
        BiPredicate<Context, Intent> syncPredicate = (c, i) ->
                i.getAction().equals(SyncBroadcast.SYNC_PROGRESS_WHATS_NEW)
                        && course.getId().equals(i.getStringExtra(SyncBroadcast.ARG_SYNC_PROGRESS_COURSE));
        BiPredicate<Context, Intent> updatePredicate = (c, i) ->
                i.getAction().equals(DatabaseBroadcaster.MINERVA_ANNOUNCEMENT_UPDATED)
                        && course.getId().equals(i.getStringExtra(DatabaseBroadcaster.ARG_MINERVA_ANNOUNCEMENT_COURSE));
        return new BroadcastListener(this, BiPredicates.or(syncPredicate, updatePredicate));
    }
}