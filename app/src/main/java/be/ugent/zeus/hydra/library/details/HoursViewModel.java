package be.ugent.zeus.hydra.library.details;

import android.app.Application;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Requests;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;
import be.ugent.zeus.hydra.library.Library;

import java.util.Arrays;
import java.util.List;

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

    @Override
    protected Request<List<OpeningHours>> getRequest() {

        if (library == null) {
            //TODO: should this be a LiveData instead?
            throw new IllegalStateException("You must set the library before using it.");
        }

        // TODO: this is currently not cached.
        return Requests.map(new OpeningHoursRequest(library), Arrays::asList);
    }
}
