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

package be.ugent.zeus.hydra.wpi.tab.user;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.time.Duration;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.wpi.account.AccountManager;
import okhttp3.Request;

/**
 * Get a Tab user.
 *
 * @author Niko Strijbol
 */
public class TabUserRequest extends JsonOkHttpRequest<TabUser> {

    private final Context context;

    public TabUserRequest(Context context) {
        super(context, TabUser.class);
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.TAB + "users/" + AccountManager.getUsername(context);
    }

    @Override
    protected Request.Builder constructRequest(@NonNull Bundle arguments) {
        Request.Builder builder = super.constructRequest(arguments);
        builder.addHeader("Authorization", "Bearer " + AccountManager.getTabKey(context));
        return builder;
    }

    @Override
    public Duration getCacheDuration() {
        // Do not cache this at the moment.
        return Duration.ZERO;
    }
}
