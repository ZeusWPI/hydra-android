package be.ugent.zeus.hydra.resto.history;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoMenu;
import org.threeten.bp.LocalDate;

import java.util.Locale;


/**
 * Request the menu for a single day.
 *
 * @author Niko Strijbol
 */
public class DayRequest extends JsonSpringRequest<RestoMenu> {

    private static final String OVERVIEW_URL = Endpoints.ZEUS_RESTO_URL + "menu/%s/%d/%d/%d.json";

    private LocalDate date;
    private RestoChoice choice;

    DayRequest() {
        super(RestoMenu.class);
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setChoice(RestoChoice choice) {
        this.choice = choice;
    }

    public boolean isSetup() {
        return date != null && choice != null;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        if (date == null) {
            throw new IllegalStateException("The date MUST be set before using the request.");
        }
        if (choice == null) {
            throw new IllegalStateException("The resto choice MUST be set before using the request.");
        }
        return String.format(Locale.US, OVERVIEW_URL, choice.getEndpoint(), date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }
}