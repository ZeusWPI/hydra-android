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

package be.ugent.zeus.hydra.resto.meta.selectable;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.meta.MetaRequest;
import be.ugent.zeus.hydra.resto.meta.RestoMeta;

/**
 * @author Niko Strijbol
 */
public class SelectableMetaRequest implements Request<List<RestoChoice>> {

    private final Request<RestoMeta> resultRequest;

    public SelectableMetaRequest(Context context) {
        this.resultRequest = new MetaRequest(context);
    }

    @NonNull
    @Override
    public Result<List<RestoChoice>> execute(@NonNull Bundle args) {
        return resultRequest.execute(args).map(restoMeta -> restoMeta.locations.stream()
                .filter(resto -> !TextUtils.isEmpty(resto.getEndpoint()))
                .map(resto -> new RestoChoice(resto.getName(), resto.getEndpoint()))
                .collect(Collectors.toCollection(ArrayList::new)));
    }
}