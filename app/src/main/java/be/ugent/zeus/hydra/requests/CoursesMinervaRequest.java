package be.ugent.zeus.hydra.requests;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.models.minerva.Courses;

import static be.ugent.zeus.hydra.loader.cache.Cache.ONE_HOUR;

/**
 * Created by feliciaan on 21/06/16.
 */
public class CoursesMinervaRequest extends MinervaRequest<Courses> {

    public CoursesMinervaRequest(HydraApplication app) {
        super(Courses.class, app);
    }

    @Override
    public String getCacheKey() {
        return "minerva_courses";
    }

    @Override
    protected String getAPIUrl() {
        return MINERVA_API + "courses";
    }

    @Override
    public long getCacheDuration() {
        return ONE_HOUR * 2;
    }
}
