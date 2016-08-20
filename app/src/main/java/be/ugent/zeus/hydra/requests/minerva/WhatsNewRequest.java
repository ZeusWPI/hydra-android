package be.ugent.zeus.hydra.requests.minerva;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.WhatsNew;

import static be.ugent.zeus.hydra.cache.Cache.NEVER;

/**
 * Request to get information about a course.
 *
 * Warning: this request should not be used. The minerva data is synchronised to the database. Use that instead. If
 * you need new data, request a sync.
 *
 * This request is, as a consequence of the above, not cached.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class WhatsNewRequest extends MinervaRequest<WhatsNew> {

    public static final String BASE_KEY = "whatsnewRequest";

    private Course course;

    public WhatsNewRequest(Course course, Context context, @Nullable Activity activity) {
        super(WhatsNew.class, context, activity);
        this.course = course;
    }

    @Override
    @NonNull
    public String getCacheKey() {
        return BASE_KEY + "." + course.getId();
    }

    @Override
    @NonNull
    protected String getAPIUrl() {
        return MINERVA_API + "course/" + course.getId() + "/whatsnew";
    }

    @Override
    public long getCacheDuration() {
        return NEVER;
    }
}