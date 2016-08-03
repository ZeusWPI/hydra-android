package be.ugent.zeus.hydra.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @author Niko Strijbol
 * @version 1/08/2016
 */
public class MinervaService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return (new MinervaAuthenticator(this)).getIBinder();
    }
}