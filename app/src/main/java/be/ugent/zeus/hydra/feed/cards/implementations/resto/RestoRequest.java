package be.ugent.zeus.hydra.feed.cards.implementations.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Requests;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardRepository;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;
import be.ugent.zeus.hydra.resto.menu.MenuFilter;
import be.ugent.zeus.hydra.resto.menu.MenuRequest;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class RestoRequest extends HideableHomeFeedRequest {

    private final Request<List<RestoMenu>> request;
    private final Context context;

    public RestoRequest(Context context, CardRepository cardRepository) {
        super(cardRepository);
        this.request = Requests.cachedList(context, new MenuRequest(context))
                .map(new MenuFilter(context));
        this.context = context.getApplicationContext();
    }

    @Override
    public int getCardType() {
        return Card.Type.RESTO;
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@Nullable Bundle args) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String restoKey = preferences.getString(RestoPreferenceFragment.PREF_RESTO_KEY, RestoPreferenceFragment.PREF_DEFAULT_RESTO);
        String restoName = preferences.getString(RestoPreferenceFragment.PREF_RESTO_NAME, context.getString(R.string.resto_default_name));
        RestoChoice choice = new RestoChoice(restoName, restoKey);
        return request.performRequest(args).map(restoMenus -> StreamSupport.stream(restoMenus)
                .map(restoMenu -> new RestoMenuCard(restoMenu, choice)));
    }
}