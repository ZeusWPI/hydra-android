package be.ugent.zeus.hydra.minerva.announcement.sync;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.common.MinervaRequest;

/**
 * Request to get information about a course.
 *
 * Warning: this is an internal sync request, and should not be used to display data directly.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class WhatsNewRequest extends MinervaRequest<ApiWhatsNew> {

    private Course course;

    WhatsNewRequest(Course course, Context context, Account account) {
        super(ApiWhatsNew.class, context, account);
        this.course = course;
    }

    @Override
    @NonNull
    protected String getAPIUrl() {
        return MINERVA_API + "course/" + course.getId() + "/whatsnew";
    }
}