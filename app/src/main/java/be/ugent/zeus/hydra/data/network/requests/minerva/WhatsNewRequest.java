package be.ugent.zeus.hydra.data.network.requests.minerva;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.data.models.minerva.WhatsNew;

/**
 * Request to get information about a course.
 *
 * Warning: this is an internal sync request, and should not be used to display data directly.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class WhatsNewRequest extends MinervaRequest<WhatsNew> {

    private Course course;

    public WhatsNewRequest(Course course, Context context, Account account) {
        super(WhatsNew.class, context, account);
        this.course = course;
    }

    @Override
    @NonNull
    protected String getAPIUrl() {
        return MINERVA_API + "course/" + course.getId() + "/whatsnew";
    }
}