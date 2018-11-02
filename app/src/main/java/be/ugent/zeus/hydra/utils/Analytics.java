package be.ugent.zeus.hydra.utils;

/**
 * Contains constants and utils for working with Firebase Analytics.
 *
 * @author Niko Strijbol
 */
@Deprecated
public class Analytics {

    /**
     * Special {@link com.google.firebase.analytics.FirebaseAnalytics.Param#CONTENT_TYPE}s. These are only used if the
     * item is viewed in a specialized view, e.g. this does not apply to things beging shown in the home feed.
     */
    public interface Type {
        String NEWS_ARTICLE = "news_article";
        String LIBRARY = "library_information";
        String SKO_ARTIST = "sko_artist";
        String RESTO_MENU = "resto_menu";
        String EVENT = "association_event";
    }

}
