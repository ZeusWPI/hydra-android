package be.ugent.zeus.hydra.common.analytics;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * These classes use the initialization-on-demand holder idiom.
 *
 * @author Niko Strijbol
 */
final class FirebaseEvents implements BaseEvents {

    private static class LazyHolder {
        static final FirebaseEvents EVENTS = new FirebaseEvents();
        static final FirebaseParams PARAMS = new FirebaseParams();
    }

    static FirebaseEvents getInstance() {
        return LazyHolder.EVENTS;
    }

    @Override
    public Params params() {
        return LazyHolder.PARAMS;
    }

    @Override
    public String login() {
        return FirebaseAnalytics.Event.LOGIN;
    }

    @Override
    public String search() {
        return FirebaseAnalytics.Event.SEARCH;
    }

    @Override
    public String selectContent() {
        return FirebaseAnalytics.Event.SELECT_CONTENT;
    }

    @Override
    public String share() {
        return FirebaseAnalytics.Event.SHARE;
    }

    @Override
    public String tutorialBegin() {
        return FirebaseAnalytics.Event.TUTORIAL_BEGIN;
    }

    @Override
    public String tutorialComplete() {
        return FirebaseAnalytics.Event.TUTORIAL_COMPLETE;
    }

    @Override
    public String viewItem() {
        return FirebaseAnalytics.Event.VIEW_ITEM;
    }

    @Override
    public String viewItemList() {
        return FirebaseAnalytics.Event.VIEW_ITEM_LIST;
    }

    private final static class FirebaseParams implements BaseEvents.Params {

        @Override
        public String method() {
            return FirebaseAnalytics.Param.METHOD;
        }

        @Override
        public String searchTerm() {
            return FirebaseAnalytics.Param.SEARCH_TERM;
        }

        @Override
        public String contentType() {
            return FirebaseAnalytics.Param.CONTENT_TYPE;
        }

        @Override
        public String itemId() {
            return FirebaseAnalytics.Param.ITEM_ID;
        }

        @Override
        public String itemName() {
            return FirebaseAnalytics.Param.ITEM_NAME;
        }

        @Override
        public String itemCategory() {
            return FirebaseAnalytics.Param.ITEM_CATEGORY;
        }
    }
}