package be.ugent.zeus.hydra.requests;

import android.content.Context;
import be.ugent.zeus.hydra.models.minerva.Courses;

/**
 * Created by feliciaan on 21/06/16.
 */
public class CoursesMinervaRequest extends MinervaRequest<Courses>{

    public CoursesMinervaRequest(Context context) {
        super(Courses.class, context);
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
        return 0;
    }
}
