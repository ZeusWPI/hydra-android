package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.association.Activity;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Created by silox on 18/04/16.
 */
public class AssociationActivityCard extends HomeCard {

    private Activity activity;

    public AssociationActivityCard(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getPriority() {
        Duration duration = new Duration(new DateTime(), new DateTime(getActivity().getStartDate()));
        return 950 - Math.max(0, (int)duration.getStandardHours()) * 4; //see 10 days in to the future
    }

    @Override
    public int getCardType() {
        return CardType.ACTIVITY;
    }

    public Activity getActivity() {
        return activity;
    }
}
