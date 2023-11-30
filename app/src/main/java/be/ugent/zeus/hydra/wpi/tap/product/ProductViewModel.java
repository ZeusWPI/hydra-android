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

package be.ugent.zeus.hydra.wpi.tap.product;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.preference.PreferenceManager;

import java.util.List;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

/**
 * @author Niko Strijbol
 */
public class ProductViewModel extends RequestViewModel<ProductData> {
    
    private final MediatorLiveData<Pair<List<Product>, Integer>> favouriteProductLiveData = new MediatorLiveData<>();

    public ProductViewModel(Application application) {
        super(application);
    }

    @NonNull
    @Override
    protected Request<ProductData> request() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        return new ProductRequest(getApplication()).map(products -> new ProductData(preferences, products));
    }
    
    public LiveData<Result<List<Product>>> getFilteredData() {
        return Transformations.map(data(), input -> input.map(ProductData::getFilteredData));
    }
    
    public void updateValue(List<Product> products) {
        Pair<List<Product>, Integer> existing = favouriteProductLiveData.getValue();
        Pair<List<Product>, Integer> newPair;
        if (existing != null) {
            newPair = Pair.create(products, existing.second);
        } else {
            newPair = Pair.create(products, null);
        }
        favouriteProductLiveData.setValue(newPair);
    }

    public void updateValue(Integer id) {
        Pair<List<Product>, Integer> existing = favouriteProductLiveData.getValue();
        Pair<List<Product>, Integer> newPair;
        if (existing != null) {
            newPair = Pair.create(existing.first, id);
        } else {
            newPair = Pair.create(null, id);
        }
        favouriteProductLiveData.setValue(newPair);
    }
    
    public LiveData<Pair<List<Product>, Integer>> getFavouriteProduct() {
        return favouriteProductLiveData;
    }
}
