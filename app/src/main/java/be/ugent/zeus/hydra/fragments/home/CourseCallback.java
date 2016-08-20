package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.minerva.Courses;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.minerva.CoursesMinervaRequest;
import be.ugent.zeus.hydra.requests.minerva.MinervaRequest;

import java.util.Collections;
import java.util.List;

/**
 * Callback for Minerva courses.
 *
 * @author Niko Strijbol
 */
class CourseCallback extends HomeLoaderCallback<Courses> {

    private static final String TAG = "CourseCallback";

    //private AnnouncementCallback announcementCallback;

    public CourseCallback(Context context, HomeCardAdapter adapter, FragmentCallback callback) {
        super(context, adapter, callback);
    //    announcementCallback = new AnnouncementCallback();
    }

    @Override
    protected List<HomeCard> convertData(@NonNull Courses data) {
      //  announcementCallback.cards.clear();
      //  return announcementCallback.cards;
        return Collections.emptyList();
    }

    @Override
    protected int getCardType() {
        return HomeCard.CardType.MINERVA_ANNOUNCEMENT;
    }

    @Override
    protected MinervaRequest<Courses> getCacheRequest() {
        //We pass null for the activity, since we don't want ask the user anything here.
        return new CoursesMinervaRequest(this.context, null);
    }

    @Override
    protected int getErrorName() {
        return R.string.fragment_home_error_minerva;
    }

//    /**
//     * Receive the announcements.
//     */
//    private class AnnouncementCallback implements WhatsNewRequest.AnnouncementsListener {
//
//        private List<HomeCard> cards = new ArrayList<>();
//
//        @Override
//        public void onAnnouncementsAdded(WhatsNew whatsNew, Course course) {
//            if (!whatsNew.getAnnouncements().isEmpty()) {
//                cards.add(new MinervaAnnouncementsCard(whatsNew.getAnnouncements(), course));
//                adapter.updateCardItems(cards, HomeCard.CardType.MINERVA_ANNOUNCEMENT);
//            }
//        }
//
//        @Override
//        public void completed() {
//            //OK
//            Log.d("Home", "Done minerva.");
//        }
//
//        @Override
//        public void error(Throwable e) {
//            //TODO: add error card for Minerva.
//            Log.w(TAG, "Some announcements could not be loaded.", e);
//        }
//    }
}