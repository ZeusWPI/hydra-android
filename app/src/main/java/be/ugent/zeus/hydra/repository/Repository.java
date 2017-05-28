package be.ugent.zeus.hydra.repository;

import android.arch.lifecycle.LiveData;
import be.ugent.zeus.hydra.data.network.Request;

/**
 * Basic repository.
 *
 * @author Niko Strijbol
 */
public interface Repository<M> {

    LiveData<Result<M>> load(Request<M> request);

}
