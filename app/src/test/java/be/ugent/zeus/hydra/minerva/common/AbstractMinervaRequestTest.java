package be.ugent.zeus.hydra.minerva.common;

import android.accounts.Account;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.minerva.account.MinervaConfig;

import static org.mockito.Mockito.doReturn;

/**
 * This mocks out getting the tokens, to allow concentrating on getting and parsing the data
 * correctly.
 *
 * @author Niko Strijbol
 */
public abstract class AbstractMinervaRequestTest<D> extends AbstractJsonRequestTest<D> {

    @Override
    protected JsonOkHttpRequest<D> spyForNormal(JsonOkHttpRequest<D> request) {
        MinervaRequest<D> fixture = (MinervaRequest<D>) super.spyForNormal(request);
        try {
            doReturn("test-token").when(fixture).getToken();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fixture;
    }

    @Override
    protected abstract MinervaRequest<D> getRequest();

    protected Account getAccount() {
        return new Account("TEST", MinervaConfig.ACCOUNT_TYPE);
    }
}