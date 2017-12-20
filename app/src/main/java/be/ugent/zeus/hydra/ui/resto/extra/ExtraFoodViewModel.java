package be.ugent.zeus.hydra.ui.resto.extra;

import android.app.Application;

import be.ugent.zeus.hydra.data.network.requests.resto.ExtraFoodRequest;
import be.ugent.zeus.hydra.domain.models.resto.ExtraFood;
import be.ugent.zeus.hydra.domain.models.resto.Food;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.repository.requests.Requests;
import be.ugent.zeus.hydra.ui.common.AdapterOutOfBoundsException;
import be.ugent.zeus.hydra.ui.common.RequestViewModel;

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
        return Requests.cache(getApplication(), new ExtraFoodRequest());
    }

    /**
     * TODO: can we merge this logic with the adapter?
     *
     * Gets the data depending on the position of the tab. Must agree with the {@link be.ugent.zeus.hydra.ui.resto.extra.ExtraFoodPagerAdapter}.
     *
     * @param position The position. Must be [0-2].
     * @param data The data.
     * @return The result.
     */
    public List<Food> getFor(int position, ExtraFood data) {
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