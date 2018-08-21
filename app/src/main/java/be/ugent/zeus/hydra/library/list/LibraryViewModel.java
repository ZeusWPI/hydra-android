package be.ugent.zeus.hydra.library.list;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.Bundle;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.arch.data.RequestLiveData;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RefreshViewModel;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.details.OpeningHours;
import be.ugent.zeus.hydra.library.details.OpeningHoursRequest;
import java9.util.Maps;
import java9.util.Optional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Strijbol
 */
public class LibraryViewModel extends RefreshViewModel<List<Library>> {

    private final Map<String, LiveData<Result<Optional<OpeningHours>>>> mapping = new HashMap<>();

    public LibraryViewModel(Application application) {
        super(application);
    }

    @Override
    protected BaseLiveData<Result<List<Library>>> constructDataInstance() {
        return new LibraryLiveData(getApplication());
    }

    LiveData<Result<Optional<OpeningHours>>> getOpeningHours(Library library) {
        return Maps.computeIfAbsent(mapping, library.getCode(), s -> {
            OpeningHoursRequest request = new OpeningHoursRequest(getApplication(), library);
            return new RequestLiveData<>(getApplication(), request.map(openingHours -> {
                if (openingHours == null || openingHours.isEmpty()) {
                    return Optional.empty();
                } else {
                    return Optional.of(openingHours.get(0));
                }
            }));
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
        for (LiveData<?> liveData: mapping.values()){
            if (liveData instanceof BaseLiveData) {
                ((BaseLiveData<?>) liveData).flagForRefresh();
            }
        }
    }

    @Override
    public void requestRefresh(Bundle args) {
        super.requestRefresh(args);
        for (LiveData<?> liveData: mapping.values()){
            if (liveData instanceof BaseLiveData) {
                ((BaseLiveData<?>) liveData).flagForRefresh(args);
            }
        }
    }
}