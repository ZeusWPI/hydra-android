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
import be.ugent.zeus.hydra.library.favourites.FavouritesRepository;
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
    public int getCardType() {
        return Card.Type.LIBRARY;
    }

    @NonNull
    @Override
    public Result<Stream<Card>> execute(@NonNull Bundle args) {

        // Get which libraries we want.
        FavouritesRepository repository = Database.get(context).getFavouritesRepository();
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