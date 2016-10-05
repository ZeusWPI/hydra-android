package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loaders.AbstractAsyncLoader;
import be.ugent.zeus.hydra.loaders.LoaderException;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
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
class MinervaCallback extends AbstractCallback {

    public MinervaCallback(HomeFragment fragment, HomeCardAdapter adapter) {
        super(fragment, adapter);
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
        return new MinervaLoader(context);
    }

    private static class MinervaLoader extends AbstractAsyncLoader<List<HomeCard>> {

        private AnnouncementDao announcementDao;

        private MinervaLoader(Context context) {
            super(context);
            announcementDao = new AnnouncementDao(context);
        }

        @NonNull
        @Override
        protected List<HomeCard> getData() throws LoaderException {

            Map<Course, List<Announcement>> map = announcementDao.getUnread();
            List<HomeCard> cards = new ArrayList<>();

            for (Map.Entry<Course, List<Announcement>> entry : map.entrySet()) {
                cards.add(new MinervaAnnouncementsCard(entry.getValue(), entry.getKey()));
            }

            return cards;
        }
    }
}