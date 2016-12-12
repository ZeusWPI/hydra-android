package be.ugent.zeus.hydra.minerva.requests;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import be.ugent.zeus.hydra.models.minerva.Announcements;
import be.ugent.zeus.hydra.models.minerva.Course;

/**
 * This request retrieves all announcements for a specific course.
 * @author Niko Strijbol
 */
public class AnnouncementsRequest extends MinervaRequest<Announcements> {

    private final Course course;

    public AnnouncementsRequest(Context context, Account account, @Nullable Activity activity, Course course) {
        super(Announcements.class, context, account, activity);
        this.course = course;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return MINERVA_API + "course/" + course.getId() + "/announcement";
    }
}