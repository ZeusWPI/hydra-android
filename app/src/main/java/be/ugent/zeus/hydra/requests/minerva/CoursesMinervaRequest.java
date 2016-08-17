package be.ugent.zeus.hydra.requests.minerva;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.models.minerva.Courses;

import static be.ugent.zeus.hydra.cache.Cache.ONE_WEEK;

/**
 * @author feliciaan
 * @author Niko Strijbol
 */
public class CoursesMinervaRequest extends MinervaRequest<Courses> {

    public static final String BASE_KEY = "minerva_courses";

    public CoursesMinervaRequest(Context context, @Nullable Activity activity) {
        super(Courses.class, context, activity);
    }

    public CoursesMinervaRequest(Context context, Account account, Activity activity) {
        super(Courses.class, context, account, activity);
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