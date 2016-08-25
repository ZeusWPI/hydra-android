package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.util.Log;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loader.AbstractAsyncLoader;
import be.ugent.zeus.hydra.loader.LoaderException;
import be.ugent.zeus.hydra.loader.ThrowableEither;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.MinervaAnnouncementsCard;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Callback for Minerva courses.
 *
 * @author Niko Strijbol
 */
class CourseCallback extends HomeLoaderCallback {

    private static final String TAG = "CourseCallback";

    public CourseCallback(Context context, HomeCardAdapter adapter, FragmentCallback callback) {
        super(context, adapter, callback);
    }

    @Override
    protected int getCardType() {
        return HomeCard.CardType.MINERVA_ANNOUNCEMENT;
    }

    @Override
    protected int getErrorName() {
        return R.string.fragment_home_error_minerva;
    }

    @Override
    public Loader<ThrowableEither<List<HomeCard>>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Called");
        return new DoaLoader(context);
    }

    private static class DoaLoader extends AbstractAsyncLoader<List<HomeCard>> {

        private AnnouncementDao announcementDao;

        /**
         * This loader has the option to ignore the cache.
         *
         * @param context The context.
         */
        private DoaLoader(Context context) {
            super(context);
            announcementDao = new AnnouncementDao(context);
        }

        @NonNull
        @Override
        protected List<HomeCard> getData() throws LoaderException {

            Map<Course, List<Announcement>> map = announcementDao.getUnread(true);
            List<HomeCard> cards = new ArrayList<>();

            for (Map.Entry<Course, List<Announcement>> entry: map.entrySet()) {
                cards.add(new MinervaAnnouncementsCard(entry.getValue(), entry.getKey()));
            }

            Log.d(TAG, cards.toString());

            return cards;
        }
    }
}