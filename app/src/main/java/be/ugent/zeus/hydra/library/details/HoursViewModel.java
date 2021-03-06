package be.ugent.zeus.hydra.library.details;

import android.app.Application;
import androidx.annotation.NonNull;

import java.util.List;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;
import be.ugent.zeus.hydra.library.Library;

/**
 * @author Niko Strijbol
 */
public class HoursViewModel extends RequestViewModel<List<OpeningHours>> {

    private Library library;

    public HoursViewModel(Application application) {
        super(application);
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    @NonNull
    @Override
    protected Request<List<OpeningHours>> getRequest() {
        if (library == null) {
            throw new IllegalStateException("You must set the library before using it.");
        }
        return new OpeningHoursRequest(getApplication(), library);
    }
}
