package be.ugent.zeus.hydra.ui.main.library;

import android.app.Application;

import be.ugent.zeus.hydra.data.models.library.Library;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class LibraryViewModel extends RefreshViewModel<List<Library>> {

    public LibraryViewModel(Application application) {
        super(application);
    }

    @Override
    protected BaseLiveData<Result<List<Library>>> constructDataInstance() {
        return new LibraryLiveData(getApplication());
    }
}