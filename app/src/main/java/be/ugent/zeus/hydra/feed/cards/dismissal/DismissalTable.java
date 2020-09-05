package be.ugent.zeus.hydra.feed.cards.dismissal;

/**
 * Contract for the table with the agenda items. This represents the current table and column names. These might change,
 * so you CANNOT use this class in migrations.
 *
 * For a description of the fields, see {@link CardDismissal}.
 *
 * @author Niko Strijbol
 */
public final class DismissalTable {

    public static final String TABLE_NAME = "feed_dismissals";

    private DismissalTable() {
        // No instances.
    }

    public interface Columns {
        String CARD_TYPE = "card_type";
        String IDENTIFIER = "card_identifier";
        String DISMISSAL_DATE = "dismissal_date";
    }
}