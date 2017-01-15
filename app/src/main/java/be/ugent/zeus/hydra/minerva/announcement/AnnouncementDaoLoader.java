package be.ugent.zeus.hydra.minerva.announcement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loaders.BroadcastLoader;
import be.ugent.zeus.hydra.loaders.LoaderException;
import be.ugent.zeus.hydra.minerva.sync.SyncBroadcast;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class AnnouncementDaoLoader extends BroadcastLoader<List<Announcement>> {

    private AnnouncementDao dao;
    private Course course;

    /**
     * This loader has the option to ignore the cache.
     *
     * @param context The context.
     */
    public AnnouncementDaoLoader(Context context, AnnouncementDao dao, Course course) {
        super(context, new IntentFilter(SyncBroadcast.SYNC_PROGRESS_WHATS_NEW));
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
    protected List<Announcement> loadData() throws LoaderException {
        return dao.getAnnouncementsForCourse(course, true);
    }

    @Override
    protected BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (course.getId().equals(intent.getStringExtra(SyncBroadcast.ARG_SYNC_PROGRESS_COURSE))) {
                    onContentChanged();
                }
            }
        };
    }
}