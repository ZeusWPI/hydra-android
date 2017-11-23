package be.ugent.zeus.hydra.ui.library;

import android.app.Application;
import be.ugent.zeus.hydra.domain.models.library.Library;
import be.ugent.zeus.hydra.domain.models.library.OpeningHours;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.repository.requests.Requests;
import be.ugent.zeus.hydra.data.network.requests.library.OpeningHoursRequest;
import be.ugent.zeus.hydra.ui.common.RequestViewModel;

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
