package be.ugent.zeus.hydra.utils;

/**
 * Contains constants and utils for working with Firebase Analytics.
 *
 * @author Niko Strijbol
 */
public class Analytics {

    /**
     * Special {@link com.google.firebase.analytics.FirebaseAnalytics.Param#CONTENT_TYPE}s. These are only used if the
     * item is viewed in a specialized view, e.g. this does not apply to things beging shown in the home feed.
     */
    public interface Type {
        String SCHAMPER_ARTICLE = "schamper_article";
        String NEWS_ARTICLE = "news_article";
        String LIBRARY = "library_information";
        String SKO_ARTIST = "sko_artist";
        String RESTO_MENU = "resto_menu";
        /**
         * Note: this value is added for completeness. You should NOT report which courses the user views, as this
         * might be used to identify the user.
         *
         * You MAY report that the user is viewing a general course.
         */
        String MINERVA_COURSE = "minerva_course";
        /**
         * Note: this value is added for completeness. You should NOT report which announcements the user views, as this
         * might be used to identify the user.
         */
        String MINERVA_ANNOUNCEMENT = "minerva_announcements";
    }

}
