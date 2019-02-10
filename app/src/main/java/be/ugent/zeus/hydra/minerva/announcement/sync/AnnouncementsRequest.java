package be.ugent.zeus.hydra.minerva.announcement.sync;

import android.accounts.Account;
import android.content.Context;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.minerva.common.MinervaRequest;
import be.ugent.zeus.hydra.minerva.course.Course;

import static be.ugent.zeus.hydra.common.network.Endpoints.MINERVA;

/**
 * This request retrieves all announcements for a specific course.
 *
 * @author Niko Strijbol
 */
class AnnouncementsRequest extends MinervaRequest<ApiAnnouncements> {

    private final Course course;

    AnnouncementsRequest(Context context, Account account, Course course) {
        super(context, ApiAnnouncements.class, account);
        this.course = course;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return MINERVA + "course/" + course.getId() + "/announcement";
    }
}