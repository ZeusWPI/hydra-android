package be.ugent.zeus.hydra.minerva.auth.requests.data;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.models.minerva.Hello;

/**
 * Simple request to Minerva. A potential use is testing the validity of account data and tokens.
 *
 * @author feliciaan
 */
public class HelloMinervaRequest extends MinervaRequest<Hello> {

    public HelloMinervaRequest(Context context, @Nullable Account account, @Nullable Activity activity) {
        super(Hello.class, context, account, activity);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return MINERVA_API + "hello";
    }
}