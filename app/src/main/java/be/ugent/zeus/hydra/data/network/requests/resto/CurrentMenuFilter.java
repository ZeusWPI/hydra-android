package be.ugent.zeus.hydra.data.network.requests.resto;

import be.ugent.zeus.hydra.domain.models.resto.RestoMenu;
import be.ugent.zeus.hydra.repository.requests.RequestFunction;
import be.ugent.zeus.hydra.repository.requests.RequestException;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class CurrentMenuFilter implements RequestFunction<List<RestoMenu>, RestoMenu> {

    @Override
    public RestoMenu apply(List<RestoMenu> restoMenus) throws RequestException {
        if (restoMenus.size() < 1) {
            throw new RequestException();
        }
        return restoMenus.get(0);
    }
}