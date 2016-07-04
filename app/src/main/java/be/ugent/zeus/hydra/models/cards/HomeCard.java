package be.ugent.zeus.hydra.models.cards;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static be.ugent.zeus.hydra.models.cards.HomeCard.CardType.*;

/**
 * Add models to the interface Created by feliciaan on 06/04/16.
 */
public abstract class HomeCard {

    /**
     * @return Priority should be a number between min inf and 1000.
     */
    public abstract int getPriority();

    /**
     * @return The card type.
     */
    @HomeCard.CardType
    public abstract int getCardType();

    /**
     * Android is horrible with enums, since Google doesn't know what they are doing apparently. Sigh.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RESTO, ACTIVITY, SPECIAL_EVENT, SCHAMPER, NEWS_ITEM})
    public @interface CardType {
        int RESTO = 1;
        int ACTIVITY = 2;
        int SPECIAL_EVENT = 3;
        int SCHAMPER = 4;
        int NEWS_ITEM = 5;
    }
}
