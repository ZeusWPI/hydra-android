package be.ugent.zeus.hydra.minerva.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

/**
 * The service for managing the Minerva account.
 *
 * This is based on the samples: <a href="https://git.io/v6CR9">sample</a>
 *
 * @author Niko Strijbol
 */
public class MinervaService extends Service {

    private MinervaAuthenticator authenticator;

    /**
     * Init the authenticator.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        authenticator = new MinervaAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}