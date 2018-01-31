package be.ugent.zeus.hydra.minerva.course.sync;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.minerva.common.MinervaRequest;

/**
 * Request to get a list of courses.
 *
 * Warning: this is an internal sync request, and should not be used to display data directly.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
class CoursesMinervaRequest extends MinervaRequest<ApiCourses> {

    CoursesMinervaRequest(Context context, Account account) {
        super(ApiCourses.class, context, account);
    }

    @Override
    @NonNull
    protected String getAPIUrl() {
        return MINERVA_API + "courses";
    }
}