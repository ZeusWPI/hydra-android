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

package be.ugent.zeus.hydra.wpi.tab.requests;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.wpi.account.AccountManager;

/**
 * @author Niko Strijbol
 */
public class TabRequestRequest extends JsonArrayRequest<TabRequest> {

    private final String status;
    private final Context context;

    /**
     * Construct a new request.
     *
     * @param context The context.
     */
    public TabRequestRequest(@NonNull Context context, @Nullable String status) {
        super(context, TabRequest.class);
        this.context = context.getApplicationContext();
        this.status = status;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        String suffix;
        if (status != null) {
            suffix = "?state=" + this.status;
        } else {
            suffix = "";
        }
        return Endpoints.TAB + "users/" + AccountManager.getUsername(context) + "/requests" + suffix;
    }

    @Override
    protected okhttp3.Request.Builder constructRequest(@NonNull Bundle arguments) {
        okhttp3.Request.Builder builder = super.constructRequest(arguments);
        builder.addHeader("Authorization", "Bearer " + AccountManager.getTabKey(context));
        return builder;
    }

    /**
     * Get a {@link Request} with acceptable Tab requests.
     * <p>
     * An "acceptable" Tab request is one issued by another user that
     * the current user can accept or decline.
     *
     * @param context The context to use for the {@link Request}.
     * @return A request instance that returns the expected Tab requests.
     * The requests will be ordered with the newest first.
     */
    public static Request<List<TabRequest>> acceptableRequests(@NonNull Context context) {
        return new TabRequestRequest(context, "open").map(allRequests ->
                allRequests
                        .stream()
                        .filter(r -> r.getActions().contains("decline") || r.getActions().contains("confirm"))
                        .sorted(Comparator.comparing(TabRequest::getTime).reversed())
                        .collect(Collectors.toList()));
    }
}
