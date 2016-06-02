package be.ugent.zeus.hydra.models;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static be.ugent.zeus.hydra.models.CardModel.CardType.ACTIVITY;
import static be.ugent.zeus.hydra.models.CardModel.CardType.RESTO;
import static be.ugent.zeus.hydra.models.CardModel.CardType.SPECIAL_EVENT;

/**
 * Add models to the interface.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public interface CardModel {

    /**
     * @return Priority should be a number between min inf and 1000.
     */
    int getPriority();

    /**
     * @return The card type.
     */
    @CardType
    int getCardType();

    /**
     * Android is horrible with enums, since Google doesn't know what they are doing apparently. Sigh.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RESTO, ACTIVITY, SPECIAL_EVENT})
    @interface CardType {
        int RESTO = 1;
        int ACTIVITY = 2;
        int SPECIAL_EVENT = 3;
    }
}