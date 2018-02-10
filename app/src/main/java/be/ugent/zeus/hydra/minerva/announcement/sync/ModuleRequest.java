package be.ugent.zeus.hydra.minerva.announcement.sync;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.minerva.common.MinervaRequest;

/**
 * @author Niko Strijbol
 */
class ModuleRequest extends MinervaRequest<ApiTools> {

    private final Course course;

    ModuleRequest(Context context, Account account, Course course) {
        super(ApiTools.class, context, account);
        this.course = course;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return MINERVA_API + "course/" + course.getId() + "/tools";
    }
}