package be.ugent.zeus.hydra.library.list;

import android.app.Application;
import android.os.Bundle;
import android.util.Pair;
import androidx.lifecycle.LiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java9.util.Maps;
import java9.util.Optional;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.arch.data.RequestLiveData;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RefreshViewModel;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.details.OpeningHours;
import be.ugent.zeus.hydra.library.details.OpeningHoursRequest;
import org.threeten.bp.LocalDate;

/**
 * @author Niko Strijbol
 */
public class LibraryViewModel extends RefreshViewModel<List<Pair<Library, Boolean>>> {

    private final Map<String, LiveData<Result<Optional<OpeningHours>>>> mapping = new HashMap<>();

    public LibraryViewModel(Application application) {
        super(application);
    }

    @Override
    protected BaseLiveData<Result<List<Pair<Library, Boolean>>>> constructDataInstance() {
        return new LibraryLiveData(getApplication());
    }

    LiveData<Result<Optional<OpeningHours>>> getOpeningHours(Library library) {
        return Maps.computeIfAbsent(mapping, library.getCode(), s -> {
            OpeningHoursRequest request = new OpeningHoursRequest(getApplication(), library);
            LocalDate today = LocalDate.now();
            return new RequestLiveData<>(getApplication(), request.forDay(today));
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mapping.clear();
    }

    @Override
    public void requestRefresh() {
        super.requestRefresh();
        for (LiveData<?> liveData : mapping.values()) {
            if (liveData instanceof BaseLiveData) {
                ((BaseLiveData<?>) liveData).flagForRefresh();
            }
        }
    }

    @Override
    public void requestRefresh(Bundle args) {
        super.requestRefresh(args);
        for (LiveData<?> liveData : mapping.values()) {
            if (liveData instanceof BaseLiveData) {
                ((BaseLiveData<?>) liveData).flagForRefresh(args);
            }
        }
    }
}