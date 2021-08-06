/*
 * Copyright (c) 2021 The Hydra authors
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

package be.ugent.zeus.hydra.feed.cards.debug;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;

import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;

import static be.ugent.zeus.hydra.feed.cards.Card.Type.DEBUG;

/**
 * @author Niko Strijbol
 */
public class WaitRequest implements HomeFeedRequest {

    private static final String TAG = "WaitRequest";

    @Override
    public int getCardType() {
        return DEBUG;
    }

    @NonNull
    @Override
    public Result<Stream<Card>> execute(@NonNull Bundle args) {
        try {
            Log.i(TAG, "execute: sleep 5 seconds.");
            Thread.sleep(5000); //Sleep 5 seconds
            return Result.Builder.fromData(Stream.empty());
        } catch (InterruptedException e) {
            return Result.Builder.fromException(new RequestException(e));
        }
    }
}