package be.ugent.zeus.hydra.data.network.requests.minerva;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.data.models.minerva.Announcements;
import be.ugent.zeus.hydra.data.models.minerva.Course;

/**
 * This request retrieves all announcements for a specific course.
 * @author Niko Strijbol
 */
public class AnnouncementsRequest extends MinervaRequest<Announcements> {

    private final Course course;

    public AnnouncementsRequest(Context context, Account account, Course course) {
        super(Announcements.class, context, account);
        this.course = course;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return MINERVA_API + "course/" + course.getId() + "/announcement";
    }
}