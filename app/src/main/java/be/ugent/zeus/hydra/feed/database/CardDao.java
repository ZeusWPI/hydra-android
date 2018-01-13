package be.ugent.zeus.hydra.feed.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.*;
import android.text.TextUtils;

import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.feed.Card;
import be.ugent.zeus.hydra.feed.CardDismissal;
import be.ugent.zeus.hydra.feed.CardIdentifier;

import java.util.Collection;
import java.util.List;

/**
 * @author Niko Strijbol
 */
@Dao
public abstract class CardDao {

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
        supportSQLiteDatabase.delete(
                DismissalTable.TABLE_NAME,
                TextUtils.join(" OR ", inSelectionArray),
                args
        );
    }
}