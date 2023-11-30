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

package be.ugent.zeus.hydra.wpi;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.arch.data.Event;
import be.ugent.zeus.hydra.common.arch.data.RequestLiveData;
import be.ugent.zeus.hydra.common.network.NetworkState;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RefreshViewModel;
import be.ugent.zeus.hydra.common.utils.ThreadingUtils;
import be.ugent.zeus.hydra.wpi.account.CombinedUser;
import be.ugent.zeus.hydra.wpi.account.CombinedUserRequest;
import be.ugent.zeus.hydra.wpi.tab.list.Transaction;
import be.ugent.zeus.hydra.wpi.tab.list.TransactionRequest;
import be.ugent.zeus.hydra.wpi.tab.requests.RequestActionRequest;
import be.ugent.zeus.hydra.wpi.tab.requests.TabRequest;
import be.ugent.zeus.hydra.wpi.tab.requests.TabRequestRequest;
import be.ugent.zeus.hydra.wpi.tap.order.CancelOrderRequest;
import be.ugent.zeus.hydra.wpi.tap.order.Order;
import be.ugent.zeus.hydra.wpi.tap.order.OrderRequest;

/**
 * This is one big view model for the WPI activity.
 * 
 * @author Niko Strijbol
 */
public class WpiViewModel extends RefreshViewModel {

    private final MutableLiveData<NetworkState> networkState;
    private final MutableLiveData<Event<Result<Boolean>>> orderRequestState;
    private final MutableLiveData<Event<Result<Boolean>>> actionRequestState;

    private LiveData<Result<List<TabRequest>>> requestData;
    private LiveData<Result<List<Transaction>>> transactionData;
    private LiveData<Result<CombinedUser>> userData;
    private LiveData<Result<List<Order>>> orderData;

    public WpiViewModel(Application application) {
        super(application);
        networkState = new MutableLiveData<>(NetworkState.IDLE);
        orderRequestState = new MutableLiveData<>();
        actionRequestState = new MutableLiveData<>();
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<Event<Result<Boolean>>> getOrderRequestResult() {
        return orderRequestState;
    }
    
    public LiveData<Event<Result<Boolean>>> getActionRequestResult() {
        return actionRequestState;
    }
    
    public LiveData<Result<List<TabRequest>>> getRequestData() {
        if (requestData == null) {
            requestData = constructDataInstance(TabRequestRequest.acceptableRequests(getApplication()));
        }
        return requestData;
    }
    
    public LiveData<Result<List<Transaction>>> getTransactionData() {
        if (transactionData == null) {
            Request<List<Transaction>> request = new TransactionRequest(getApplication())
                    .map(transactions -> transactions
                            .stream()
                            .sorted(Comparator.comparing(Transaction::time).reversed())
                            .collect(Collectors.toList()));
            transactionData = constructDataInstance(request);
        }
        return transactionData;
    }
    
    public LiveData<Result<CombinedUser>> getUserData() {
        if (userData == null) {
            userData = constructDataInstance(new CombinedUserRequest(getApplication()));
        }
        return userData;
    }
    
    public LiveData<Result<List<Order>>> getOrderData() {
        if (orderData == null) {
            orderData = constructDataInstance(new OrderRequest(getApplication()));
        }
        return orderData;
    }

    private <D> BaseLiveData<Result<D>> constructDataInstance(Request<D> request) {
        return registerSource(new RequestLiveData<>(getApplication(), request));
    }

    public void cancelOrder(Order order) {
        CancelOrderRequest request = new CancelOrderRequest(getApplication(), order);
        networkState.postValue(NetworkState.BUSY);
        ThreadingUtils.execute(() -> {
            Result<Boolean> result = request.execute();
            networkState.postValue(NetworkState.IDLE);
            orderRequestState.postValue(new Event<>(result));
        });
    }
    
    public void acceptRequest(TabRequest tabRequest) {
        RequestActionRequest request = new RequestActionRequest(getApplication(), "confirm", tabRequest);
        networkState.postValue(NetworkState.BUSY);
        ThreadingUtils.execute(() -> {
            Result<Boolean> result = request.execute();
            networkState.postValue(NetworkState.IDLE);
            actionRequestState.postValue(new Event<>(result));
        });
    }

    public void declineRequest(TabRequest tabRequest) {
        RequestActionRequest request = new RequestActionRequest(getApplication(), "decline", tabRequest);
        networkState.postValue(NetworkState.BUSY);
        ThreadingUtils.execute(() -> {
            Result<Boolean> result = request.execute();
            networkState.postValue(NetworkState.IDLE);
            actionRequestState.postValue(new Event<>(result));
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        requestData = null;
        transactionData = null;
        userData = null;
        orderData = null;
    }
}
