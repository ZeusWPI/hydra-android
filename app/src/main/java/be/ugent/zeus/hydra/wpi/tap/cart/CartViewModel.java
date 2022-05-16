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

package be.ugent.zeus.hydra.wpi.tap.cart;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import be.ugent.zeus.hydra.common.arch.data.Event;
import be.ugent.zeus.hydra.common.network.NetworkState;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;
import be.ugent.zeus.hydra.common.utils.ThreadingUtils;

/**
 * @author Niko Strijbol
 */
public class CartViewModel extends RequestViewModel<Cart> {

    private final MutableLiveData<NetworkState> networkState;
    private final MutableLiveData<Event<Result<OrderResult>>> requestResult;
    
    public CartViewModel(Application application) {
        super(application);
        networkState = new MutableLiveData<>(NetworkState.IDLE);
        requestResult = new MutableLiveData<>();
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<Event<Result<OrderResult>>> getRequestResult() {
        return requestResult;
    }

    @NonNull
    @Override
    protected Request<Cart> getRequest() {
        return new CartRequest(getApplication());
    }

    /**
     * Send the cart to the server.
     * 
     * @param cart The cart to save.
     */
    public void startRequest(Cart cart) {
        CreateOrderRequest request = new CreateOrderRequest(getApplication(), cart);
        networkState.postValue(NetworkState.BUSY);
        ThreadingUtils.execute(() -> {
            Result<OrderResult> result = request.execute();
            networkState.postValue(NetworkState.IDLE);
            requestResult.postValue(new Event<>(result));
        });
    }
}
