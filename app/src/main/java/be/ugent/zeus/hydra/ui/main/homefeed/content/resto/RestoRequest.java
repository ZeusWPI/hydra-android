package be.ugent.zeus.hydra.ui.main.homefeed.content.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.domain.models.resto.RestoMenu;
import be.ugent.zeus.hydra.data.network.requests.resto.MenuFilter;
import be.ugent.zeus.hydra.data.network.requests.resto.MenuRequest;
import be.ugent.zeus.hydra.data.network.requests.resto.SelectableMetaRequest;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.repository.requests.Requests;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import java.util.Arrays;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class RestoRequest implements HomeFeedRequest {

    private final Request<List<RestoMenu>> request;
    private final Context context;

    public RestoRequest(Context context) {
        this.request = Requests.map(
                Requests.map(Requests.cache(context, new MenuRequest(context)), Arrays::asList),
                new MenuFilter(context)
        );
        this.context = context.getApplicationContext();
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.RESTO;
    }

    @NonNull
    @Override
    public Result<Stream<HomeCard>> performRequest(Bundle args) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String restoKey = preferences.getString(RestoPreferenceFragment.PREF_RESTO_KEY, RestoPreferenceFragment.PREF_DEFAULT_RESTO);
        String restoName = preferences.getString(RestoPreferenceFragment.PREF_RESTO_NAME, context.getString(R.string.resto_default_name));
        SelectableMetaRequest.RestoChoice choice = new SelectableMetaRequest.RestoChoice(restoName, restoKey);
        return request.performRequest(args).map(restoMenus -> StreamSupport.stream(restoMenus)
                .map(restoMenu -> new RestoMenuCard(restoMenu, choice)));
    }
}