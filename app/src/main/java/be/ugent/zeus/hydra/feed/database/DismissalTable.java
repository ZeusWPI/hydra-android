package be.ugent.zeus.hydra.feed.database;

/**
 * Contract for the table with the agenda items. This represents the current table and column names. This might change,
 * so you CANNOT use these in migrations.
 *
 * @author Niko Strijbol
 */
public final class DismissalTable {

    public static final String TABLE_NAME = "feed_dismissals";

    public interface Columns {
        String CARD_TYPE = "card_type";
        String IDENTIFIER = "card_identifier";
        String DISMISSAL_DATE = "dismissal_date";
    }
}