package be.ugent.zeus.hydra.common.database;

import android.content.Context;

import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.feed.cards.CardRepository;
import be.ugent.zeus.hydra.feed.cards.database.CardDatabaseRepository;

/**
 * Provides implementations of the repository interfaces.
 *
 * @author Niko Strijbol
 */
public class RepositoryFactory {

    @NonNull
    public static synchronized CardRepository getCardRepository(Context context) {
        Database database = Database.get(context);
        return new CardDatabaseRepository(database.getCardDao());
    }
}