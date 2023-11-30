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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Objects;

import be.ugent.zeus.hydra.common.network.InstanceProvider;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * Get an existing cart if there is one available.
 * 
 * @author Niko Strijbol
 */
class ExistingCartRequest implements Request<StorageCart> {
    
    public static final String PREF_CART_STORAGE = "pref_cart_storage_v2";
    
    private final Context context;

    ExistingCartRequest(Context context) {
        this.context = context.getApplicationContext();
    }

    public static void saveCartStorage(@NonNull Context context, @NonNull StorageCart storage) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        Moshi moshi = InstanceProvider.moshi();
        JsonAdapter<StorageCart> adapter = moshi.adapter(StorageCart.class);
        String raw = adapter.toJson(storage);
        p.edit()
                .putString(PREF_CART_STORAGE, raw)
                .apply();
    }

    @NonNull
    @Override
    public Result<StorageCart> execute(@NonNull Bundle args) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        Moshi moshi = InstanceProvider.moshi();
        JsonAdapter<StorageCart> adapter = moshi.adapter(StorageCart.class);
        
        StorageCart found;
        try {
            found = Objects.requireNonNull(adapter.fromJson(p.getString(PREF_CART_STORAGE, "")));
            
            // Carts older than this are discarded.
            OffsetDateTime oldestLimit = OffsetDateTime.now().minusDays(1);
            if (found.lastEdited().isBefore(oldestLimit)) {
                found = null;
            }
        } catch (IOException | NullPointerException e) {
            found = null;
        }
        
        if (found == null) {
            found = new StorageCart(new ArrayList<>(), OffsetDateTime.now());
        }
        
        return Result.Builder.fromData(found);
    }
}
