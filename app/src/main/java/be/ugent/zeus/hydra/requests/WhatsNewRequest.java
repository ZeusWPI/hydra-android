package be.ugent.zeus.hydra.requests;

import android.content.Context;

import com.octo.android.robospice.persistence.DurationInMillis;

import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.models.minerva.WhatsNew;

/**
 * Created by feliciaan on 29/06/16.
 */
public class WhatsNewRequest extends MinervaRequest<WhatsNew> {
    private Course course;

    public WhatsNewRequest(Course course, Context context) {
        super(WhatsNew.class, context);
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
        return DurationInMillis.ONE_HOUR;
    }
}
