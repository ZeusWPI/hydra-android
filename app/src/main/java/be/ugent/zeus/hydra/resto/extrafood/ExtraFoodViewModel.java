package be.ugent.zeus.hydra.resto.extrafood;

import android.app.Application;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.AdapterOutOfBoundsException;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class ExtraFoodViewModel extends RequestViewModel<ExtraFood> {

    public ExtraFoodViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<ExtraFood> getRequest() {
        return new ExtraFoodRequest(getApplication());
    }

    /**
     * TODO: can we merge this logic with the adapter?
     *
     * Gets the data depending on the position of the tab. Must agree with the {@link ExtraFoodPagerAdapter}.
     *
     * @param position The position. Must be [0-2].
     * @param data The data.
     * @return The result.
     */
    static List<Food> getFor(int position, ExtraFood data) {
        switch (position) {
            case 0:
                return data.getBreakfast();
            case 1:
                return data.getDesserts();
            case 2:
                return data.getDrinks();
            default:
                throw new AdapterOutOfBoundsException(position, 3);
        }
    }
}