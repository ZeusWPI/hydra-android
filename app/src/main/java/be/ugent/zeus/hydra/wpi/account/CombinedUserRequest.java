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

package be.ugent.zeus.hydra.wpi.account;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.wpi.tab.user.TabUser;
import be.ugent.zeus.hydra.wpi.tab.user.TabUserRequest;
import be.ugent.zeus.hydra.wpi.tap.user.TapUser;
import be.ugent.zeus.hydra.wpi.tap.user.TapUserRequest;

/**
 * @author Niko Strijbol
 */
public class CombinedUserRequest implements Request<CombinedUser> {

    private final Request<TabUser> tabUserRequest;
    private final Request<TapUser> tapUserRequest;

    public CombinedUserRequest(Context context) {
        this.tabUserRequest = new TabUserRequest(context);
        this.tapUserRequest = new TapUserRequest(context);
    }

    @NonNull
    @Override
    public Result<CombinedUser> execute(@NonNull Bundle args) {
        return tabUserRequest.andThen(tabUser ->
                tapUserRequest.map(tapUser -> new CombinedUser(
                        tapUser.id(),
                        tapUser.name(),
                        tabUser.balance(),
                        tapUser.profileImageUrl(),
                        tapUser.orderCount(),
                        tapUser.favourite()
                ))).execute(args);
    }
}
