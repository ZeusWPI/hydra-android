/*
 * Copyright (c) 2022 Niko Strijbol
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.wpi.cammie;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import be.ugent.zeus.hydra.common.arch.data.Event;
import be.ugent.zeus.hydra.common.network.NetworkState;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.utils.ThreadingUtils;

/**
 * Responsible for managing requests related to cammie.
 * <p>
 * There are a set of methods that return LiveData. Those should be
 * listened to to get the results.
 *
 * @author Niko Strijbol
 */
public class CammieViewModel extends AndroidViewModel {

    private final MutableLiveData<NetworkState> networkState;

    public CammieViewModel(@NonNull Application application) {
        super(application);
        networkState = new MutableLiveData<>(NetworkState.IDLE);
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public void startRequest(MoveRequest.Command command) {
        MoveRequest request = new MoveRequest(getApplication(), command);
        networkState.postValue(NetworkState.BUSY);
        ThreadingUtils.execute(() -> {
            Result<String> result = request.execute();
            networkState.postValue(NetworkState.IDLE);
        });
    }
}
