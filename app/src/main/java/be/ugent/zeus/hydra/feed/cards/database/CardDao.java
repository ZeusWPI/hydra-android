package be.ugent.zeus.hydra.feed.cards.database;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteQueryBuilder;
import androidx.room.*;
import android.text.TextUtils;

import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardDismissal;
import be.ugent.zeus.hydra.feed.cards.CardIdentifier;

import java.util.Collection;
import java.util.List;

/**
 * @author Niko Strijbol
 */
@Dao
public abstract class CardDao {

    private static final String TAG = "CardDao";

    private final Database database;

    public CardDao(Database database) {
        this.database = database;
    }

    @Query("SELECT * FROM " + DismissalTable.TABLE_NAME + " WHERE " + DismissalTable.Columns.CARD_TYPE + " = :type")
    public abstract List<CardDismissal> getForType(@Card.Type int type);

    @Query("SELECT " + DismissalTable.Columns.CARD_TYPE + ", " + DismissalTable.Columns.IDENTIFIER + " FROM " + DismissalTable.TABLE_NAME + " WHERE " + DismissalTable.Columns.CARD_TYPE + " = :type")
    public abstract List<CardIdentifier> getIdsForType(@Card.Type int type);

    @Insert
    public abstract void insert(CardDismissal dismissal);

    @Update
    public abstract void update(CardDismissal cardDismissal);

    @Delete
    public abstract void delete(CardDismissal cardDismissal);

    @Query("DELETE FROM " + DismissalTable.TABLE_NAME)
    public abstract void deleteAll();

    public void deleteByIdentifier(Collection<CardIdentifier> identifiers) {

        if (identifiers.isEmpty()) {
            return;
        }

        String[] inSelectionArray = new String[identifiers.size()];
        for (int i = 0; i < identifiers.size(); i++) {
            inSelectionArray[i] = "(" + DismissalTable.Columns.CARD_TYPE + " = ? AND " + DismissalTable.Columns.IDENTIFIER + " = ?)";
        }

        Object[] args = new Object[identifiers.size() * 2];
        int counter = 0;
        for (CardIdentifier identifier : identifiers) {
            args[counter++] = identifier.getCardType();
            args[counter++] = identifier.getIdentifier();
        }

        SupportSQLiteDatabase supportSQLiteDatabase = database.getOpenHelper().getWritableDatabase();
        supportSQLiteDatabase.beginTransaction();
        try {
            supportSQLiteDatabase.delete(DismissalTable.TABLE_NAME, TextUtils.join(" OR ", inSelectionArray), args);
            supportSQLiteDatabase.setTransactionSuccessful();
        } finally {
            supportSQLiteDatabase.endTransaction();
        }
    }
}