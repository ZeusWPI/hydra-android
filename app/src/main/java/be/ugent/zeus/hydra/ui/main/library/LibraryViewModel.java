package be.ugent.zeus.hydra.ui.main.library;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.v4.util.Pair;
import be.ugent.zeus.hydra.data.models.library.Library;
import be.ugent.zeus.hydra.repository.Result;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class LibraryViewModel extends RefreshViewModel<Pair<List<Library>, List<Library>>> {

    public LibraryViewModel(Application application) {
        super(application);
    }

    @Override
    protected LiveData<Result<Pair<List<Library>, List<Library>>>> provideData() {
        return new LibraryLiveData(getApplication());
    }
}