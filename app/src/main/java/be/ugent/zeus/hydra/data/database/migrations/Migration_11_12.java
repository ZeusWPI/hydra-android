package be.ugent.zeus.hydra.data.database.migrations;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * This migration adds support for {@link be.ugent.zeus.hydra.domain.models.feed.CardDismissal}s.
 *
 * @author Niko Strijbol
 */
public class Migration_11_12 extends Migration {

    private static final String TAG = "Migration_11_12";

    public Migration_11_12() {
        super(11, 12);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {

        Log.i(TAG, "Migrating database from " + this.startVersion + " to " + this.endVersion);

        // We just need to create the new table, nothing else.
        // We don't recover existing data, since that is not possible, nor is it really necessary.
        database.execSQL(
                "CREATE TABLE `feed_dismissals` (`dismissal_date` TEXT NOT NULL, `card_type` INTEGER NOT NULL, `card_identifier` TEXT NOT NULL, PRIMARY KEY(`card_type`, `card_identifier`))"
        );
        database.execSQL(
                "CREATE  INDEX `index_feed_dismissals_card_type` ON `feed_dismissals` (`card_type`)"
        );

        Log.i(TAG, "Migration completed.");
    }
}