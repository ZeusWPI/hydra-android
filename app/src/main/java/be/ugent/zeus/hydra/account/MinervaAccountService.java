package be.ugent.zeus.hydra.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @author Niko Strijbol
 * @version 4/07/2016
 */
public class MinervaAccountService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MinervaAuthenticator authenticator = new MinervaAuthenticator(this);
        return authenticator.getIBinder();
    }
}
