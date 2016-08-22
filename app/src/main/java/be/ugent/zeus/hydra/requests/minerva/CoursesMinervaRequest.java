package be.ugent.zeus.hydra.requests.minerva;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.models.minerva.Courses;

/**
 * Request to get a list of courses.
 *
 * Warning: this request should not be used. The minerva data is synchronised to the database. Use that instead. If
 * you need new data, request a sync.
 *
 * This request is, as a consequence of the above, not cached.
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
    protected String getAPIUrl() {
        return MINERVA_API + "courses";
    }
}