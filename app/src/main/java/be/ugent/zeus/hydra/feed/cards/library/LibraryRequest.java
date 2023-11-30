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

package be.ugent.zeus.hydra.feed.cards.library;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.library.details.OpeningHoursRequest;
import be.ugent.zeus.hydra.library.favourites.LibraryFavourite;

/**
 * This request will retrieve a list of opening hours for each library that is marked as favourite by the user.
 * <br>
 * Actually getting the hours is done by using {@link OpeningHoursRequest#forDay(LocalDate)}. This request will then
 * combine the retrieved data with the titles of the libraries. This way we don't have to retrieve the full list of
 * libraries to get the name.
 *
 * @author Niko Strijbol
 */
public class LibraryRequest implements HomeFeedRequest {

    private static final String TAG = "LibraryRequest";

    private final Context context;

    public LibraryRequest(Context context) {
        this.context = context;
    }

    @Override
    public int cardType() {
        return Card.Type.LIBRARY;
    }

    @NonNull
    @Override
    public Result<Stream<Card>> execute(@NonNull Bundle args) {

        // Get which libraries we want.
        var repository = Database.get(context).getFavouriteRepository();
        List<LibraryFavourite> favourites = repository.getAll();

        if (favourites.isEmpty()) {
            Log.d(TAG, "No favourite libraries, skipping card.");
            return Result.Builder.fromData(Stream.empty());
        }

        // Get the opening hours for each library.
        LocalDate today = LocalDate.now();

        return Result.Builder.fromData(Stream.of(new LibraryCard(favourites.stream()
                .map(favourite -> {
                    OpeningHoursRequest r = new OpeningHoursRequest(context, favourite.getCode());
                    return Pair.create(favourite.getName(), r.forDay(today).execute());
                })
                .collect(Collectors.toList()))));
    }
}