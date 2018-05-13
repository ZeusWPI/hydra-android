package be.ugent.zeus.hydra.minerva.announcement.sync;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.common.MinervaRequest;

import static be.ugent.zeus.hydra.common.network.Endpoints.MINERVA;

/**
 * @author Niko Strijbol
 */
class ModuleRequest extends MinervaRequest<ApiTools> {

    private final Course course;

    ModuleRequest(Context context, Account account, Course course) {
        super(context, ApiTools.class, account);
        this.course = course;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return MINERVA + "course/" + course.getId() + "/tools";
    }
}