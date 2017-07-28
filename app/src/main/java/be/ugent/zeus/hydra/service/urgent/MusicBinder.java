package be.ugent.zeus.hydra.service.urgent;

import android.os.Binder;

/**
 * A simple binder that gives access to the service.
 *
 * @author Niko Strijbol
 */
@Deprecated
public class MusicBinder extends Binder {

    private final MusicService service;

    public MusicBinder(MusicService service) {
        this.service = service;
    }

    /**
     * @return Get the music service.
     */
    public MusicService getService() {
        return service;
    }
}