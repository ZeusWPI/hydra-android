package be.ugent.zeus.hydra.requests.minerva;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.models.minerva.Courses;

import static be.ugent.zeus.hydra.loader.cache.Cache.ONE_WEEK;

/**
 * @author feliciaan
 * @author Niko Strijbol
 */
public class CoursesMinervaRequest extends MinervaRequest<Courses> {

    public static final String BASE_KEY = "minerva_courses";

    public CoursesMinervaRequest(HydraApplication app) {
        super(Courses.class, app);
    }

    @Override
    @NonNull
    public String getCacheKey() {
        return BASE_KEY;
    }

    @Override
    @NonNull
    protected String getAPIUrl() {
        return MINERVA_API + "courses";
    }

    @Override
    public long getCacheDuration() {
        return ONE_WEEK * 4;
    }
}