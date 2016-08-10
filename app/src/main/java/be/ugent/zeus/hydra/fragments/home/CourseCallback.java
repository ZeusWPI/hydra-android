package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.MinervaAnnouncementsCard;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.Courses;
import be.ugent.zeus.hydra.models.minerva.WhatsNew;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.minerva.CoursesMinervaRequest;
import be.ugent.zeus.hydra.requests.minerva.MinervaRequest;
import be.ugent.zeus.hydra.requests.minerva.WhatsNewRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback for Minerva courses.
 *
 * @author Niko Strijbol
 */
class CourseCallback extends HomeLoaderCallback<Courses> {

    private static final String TAG = "CourseCallback";

    private AnnouncementCallback announcementCallback;

    public CourseCallback(Context context, HomeCardAdapter adapter, ProgressCallback callback) {
        super(context, adapter, callback);
        announcementCallback = new AnnouncementCallback();
    }

    /**
     * Convert the loaded data to a list of home cards. This method may be called in a different thread.
     * <p>
     * TODO: should this be executed in a separate thread, or should we wrap the request in a new request that already
     * TODO: takes care of this. For now, we just do this on the main thread.
     *
     * @param data The loaded data.
     *
     * @return The converted data.
     */
    @Override
    protected List<HomeCard> convertData(@NonNull Courses data) {
        announcementCallback.cards.clear();
        WhatsNewRequest.getAllAnnouncements(data, this.context, null, announcementCallback);
        return announcementCallback.cards;
    }

    /**
     * @return The card type of the cards that are produced here.
     */
    @Override
    protected int getCardType() {
        return HomeCard.CardType.MINERVA_ANNOUNCEMENT;
    }

    @Override
    protected MinervaRequest<Courses> getCacheRequest() {
        //We pass null for the activity, since we don't want ask the user anything here.
        return new CoursesMinervaRequest(this.context, null);
    }

    /**
     * Receive the announcements.
     */
    private class AnnouncementCallback implements WhatsNewRequest.AnnouncementsListener {

        private List<HomeCard> cards = new ArrayList<>();

        @Override
        public void onAnnouncementsAdded(WhatsNew whatsNew, Course course) {
            if(!whatsNew.getAnnouncements().isEmpty()) {
                cards.add(new MinervaAnnouncementsCard(whatsNew.getAnnouncements(), course));
                adapter.updateCardItems(cards, HomeCard.CardType.MINERVA_ANNOUNCEMENT);
            }
        }

        @Override
        public void completed() {
            //OK
            Log.d("Home", "Done minerva.");
        }

        @Override
        public void error(Throwable e) {
            //TODO: add error card for Minerva.
            Log.w(TAG, "Some announcements could not be loaded.", e);
        }
    }
}