package be.ugent.zeus.hydra.requests;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.WhatsNew;

import static be.ugent.zeus.hydra.loader.cache.Cache.ONE_HOUR;

/**
 * Created by feliciaan on 29/06/16.
 */
public class WhatsNewRequest extends MinervaRequest<WhatsNew> {
    private Course course;

    public WhatsNewRequest(Course course, HydraApplication app) {
        super(WhatsNew.class, app);
        this.course = course;
    }

    @Override
    public String getCacheKey() {
        return "whatsnewRequest." + course.getId();
    }

    @Override
    protected String getAPIUrl() {
        return MINERVA_API + "course/" + course.getId() + "/whatsnew";
    }

    @Override
    public long getCacheDuration() {
        return ONE_HOUR;
    }
}
