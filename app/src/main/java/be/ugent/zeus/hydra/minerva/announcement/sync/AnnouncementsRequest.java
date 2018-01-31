package be.ugent.zeus.hydra.minerva.announcement.sync;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.minerva.common.MinervaRequest;
import be.ugent.zeus.hydra.minerva.course.Course;

/**
 * This request retrieves all announcements for a specific course.
 *
 * @author Niko Strijbol
 */
class AnnouncementsRequest extends MinervaRequest<ApiAnnouncements> {

    private final Course course;

    AnnouncementsRequest(Context context, Account account, Course course) {
        super(ApiAnnouncements.class, context, account);
        this.course = course;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return MINERVA_API + "course/" + course.getId() + "/announcement";
    }
}