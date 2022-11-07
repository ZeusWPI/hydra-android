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

package be.ugent.zeus.hydra.wpi.tab.create;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.arch.data.Event;
import be.ugent.zeus.hydra.common.network.NetworkState;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;
import be.ugent.zeus.hydra.common.utils.ThreadingUtils;

/**
 * Responsible for managing requests to create transactions.
 * 
 * There are a set of methods that return LiveData. Those should be
 * listened to to get the results.
 * 
 * @author Niko Strijbol
 */
public class TransactionViewModel extends RequestViewModel<List<String>> {
    
    private final MutableLiveData<NetworkState> networkState;
    private final MutableLiveData<Event<Result<Boolean>>> requestResult;
    
    public TransactionViewModel(@NonNull Application application) {
        super(application);
        networkState = new MutableLiveData<>(NetworkState.IDLE);
        requestResult = new MutableLiveData<>();
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }
    
    public LiveData<Event<Result<Boolean>>> getRequestResult() {
        return requestResult;
    }
    
    public void startRequest(TransactionForm transactionForm) {
        CreateTransactionRequest request = new CreateTransactionRequest(getApplication(), transactionForm);
        networkState.postValue(NetworkState.BUSY);
        ThreadingUtils.execute(() -> {
            Result<Boolean> result = request.execute();
            networkState.postValue(NetworkState.IDLE);
            requestResult.postValue(new Event<>(result));
        });
    }

    @NonNull
    @Override
    protected Request<List<String>> getRequest() {
        return new MemberRequest(getApplication())
            .map(members -> members
                 .stream()
                 .sorted(Comparator.comparing(Member::getName))
                 .map(Member::getName)
                 .collect(Collectors.toList()));
    }
}
