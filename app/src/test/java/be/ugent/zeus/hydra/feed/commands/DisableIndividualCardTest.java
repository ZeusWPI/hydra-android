package be.ugent.zeus.hydra.feed.commands;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;

import java.time.Instant;
import java.util.*;

import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.reporting.Tracker;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.CardDismissal;
import be.ugent.zeus.hydra.feed.cards.dismissal.CardIdentifier;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Niko Strijbol
 */
public class DisableIndividualCardTest {

    @Test
    public void testExecuteAndUndo() {
        // TODO: this relies a bit much on the internals.
        DismissalDao repository = new MemoryDismissalDao();
        CardIdentifier identifier = new CardIdentifier(Card.Type.DEBUG, "test");
        CardDismissal cardDismissal = new CardDismissal(identifier, Instant.now());
        Context context = mock(Context.class);
        MemoryTracker tracker = new MemoryTracker();
        tracker.allowAnalytics(true);
        tracker.allowCrashReporting(true);
        Reporting.setTracker(tracker);

        DisableIndividualCard disableIndividualCard = new DisableIndividualCard(cardDismissal, c -> repository);
        disableIndividualCard.execute(context);

        assertEquals(1, tracker.logged.size());

        List<CardDismissal> dismissals = repository.getForType(Card.Type.DEBUG);
        assertEquals(1, dismissals.size());
        assertEquals(identifier.getIdentifier(), dismissals.get(0).getIdentifier().getIdentifier());

        disableIndividualCard.undo(context);

        List<CardDismissal> newDismissals = repository.getForType(Card.Type.DEBUG);
        assertTrue(newDismissals.isEmpty());
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static class MemoryTracker implements Tracker {

        private final List<Event> logged = new ArrayList<>();
        private final Map<String, String> userValues = new HashMap<>();
        private final List<Throwable> errors = new ArrayList<>();

        private boolean allowingAnalytics;
        private boolean allowingCrashReporting;

        @Override
        public void log(Event event) {
            if (allowingAnalytics) {
                logged.add(event);
            }
        }

        @Override
        public void setCurrentScreen(@NonNull Activity activity, String screenName, String classOverride) {
            // do nothing
        }

        @Override
        public void setUserProperty(String name, String value) {
            userValues.put(name, value);
        }

        @Override
        public void logError(Throwable throwable) {
            if (allowingCrashReporting) {
                this.errors.add(throwable);
            }
        }

        @Override
        public void allowAnalytics(boolean allowed) {
            this.allowingAnalytics = allowed;
        }

        @Override
        public void allowCrashReporting(boolean allowed) {
            this.allowingCrashReporting = allowed;
        }
    }
}