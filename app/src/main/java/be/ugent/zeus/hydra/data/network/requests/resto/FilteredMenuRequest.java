package be.ugent.zeus.hydra.data.network.requests.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import java.util.List;

/**
 * Filtered resto request.
 *
 * @author Niko Strijbol
 */
public class FilteredMenuRequest implements Request<List<RestoMenu>> {

    private final Context context;
    private final Request<List<RestoMenu>> request;

    public FilteredMenuRequest(Context context, Request<List<RestoMenu>> request) {
        this.context = context.getApplicationContext();
        this.request = request;
    }

    @NonNull
    @Override
    public List<RestoMenu> performRequest(Bundle args) throws RequestFailureException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        LocalTime closingHour = LocalTime.parse(
                preferences.getString(
                        RestoPreferenceFragment.PREF_RESTO_CLOSING_HOUR,
                        RestoPreferenceFragment.DEFAULT_CLOSING_TIME
                )
        );
        LocalDate today = LocalDate.now();
        boolean isEarlyEnough = LocalDateTime.now().isBefore(LocalDateTime.of(LocalDate.now(), closingHour));

        return StreamSupport.stream(request.performRequest(null))
                .filter(m -> m.getDate().isAfter(today) || (m.getDate().isEqual(today) && isEarlyEnough))
                .collect(Collectors.toList());
    }
}