package be.ugent.zeus.hydra.models.cards;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.association.Activity;

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
    public HomeCardAdapter.HomeType getCardType() {
        return HomeCardAdapter.HomeType.ACTIVITY;
    }

    public Activity getActivity() {
        return activity;
    }
}
