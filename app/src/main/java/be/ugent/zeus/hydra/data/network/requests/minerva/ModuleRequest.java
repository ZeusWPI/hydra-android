package be.ugent.zeus.hydra.data.network.requests.minerva;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.dto.minerva.Tools;
import be.ugent.zeus.hydra.domain.models.minerva.Course;

/**
 * @author Niko Strijbol
 */
public class ModuleRequest extends MinervaRequest<Tools> {

    private final Course course;

    public ModuleRequest(Context context, Account account, Course course) {
        super(Tools.class, context, account);
        this.course = course;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return MINERVA_API + "course/" + course.getId() + "/tools";
    }
}