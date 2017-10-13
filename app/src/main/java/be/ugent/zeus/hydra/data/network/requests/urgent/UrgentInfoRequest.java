package be.ugent.zeus.hydra.data.network.requests.urgent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.data.models.UrgentInfo;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.repository.requests.RequestException;
import be.ugent.zeus.hydra.repository.requests.Result;

/**
 * @author Niko Strijbol
 */
public class UrgentInfoRequest implements Request<UrgentInfo> {

    @NonNull
    @Override
    public Result<UrgentInfo> performRequest(@Nullable Bundle args) {
        ProgrammeRepository repository = new ProgrammeRepository();
        return repository.getCurrent()
                .map(Result.Builder::fromData)
                .orElseGet(() -> Result.Builder.fromException(new RequestException("Error while getting urgent.fm stuff.")));
    }
}